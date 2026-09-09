package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.HazardCategory
import com.example.model.HazardReport
import com.example.ui.theme.*
import kotlin.math.*

private const val TILE_SIZE = 256

fun lonToTileXDouble(lon: Double, zoom: Int): Double {
    return (lon + 180.0) / 360.0 * (1 shl zoom)
}

fun latToTileYDouble(lat: Double, zoom: Int): Double {
    val latRad = Math.toRadians(lat)
    return (1.0 - asinh(tan(latRad)) / Math.PI) / 2.0 * (1 shl zoom)
}

fun tileXToLon(x: Double, zoom: Int): Double {
    return x / (1 shl zoom) * 360.0 - 180.0
}

fun tileYToLat(y: Double, zoom: Int): Double {
    val n = Math.PI - 2.0 * Math.PI * y / (1 shl zoom)
    return Math.toDegrees(atan(sinh(n)))
}

@Composable
fun OpenStreetMapCompose(
    modifier: Modifier = Modifier,
    centerLat: Double,
    centerLon: Double,
    zoom: Int,
    onZoomChange: (Int) -> Unit,
    onCenterChange: (Double, Double) -> Unit,
    userLat: Double,
    userLon: Double,
    reports: List<HazardReport>,
    onReportClick: (HazardReport) -> Unit,
    onRecenterClick: () -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    var activeTileStyle by remember { mutableStateOf(0) } // 0: Standard OSM, 1: CyclOSM/Topographic
    val tileUrlTemplate = remember(activeTileStyle) {
        if (activeTileStyle == 0) {
            "https://tile.openstreetmap.org"
        } else {
            "https://a.tile.openstreetmap.fr/hot"
        }
    }

    // Pulse animation for user location
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFE5E7EB))
            .pointerInput(zoom, centerLat, centerLon) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val totalTiles = (1 shl zoom).toDouble()
                    val dTileX = dragAmount.x / (TILE_SIZE * density.density)
                    val dTileY = dragAmount.y / (TILE_SIZE * density.density)

                    val curTileX = lonToTileXDouble(centerLon, zoom)
                    val curTileY = latToTileYDouble(centerLat, zoom)

                    val newTileX = curTileX - dTileX
                    val newTileY = (curTileY - dTileY).coerceIn(0.0, totalTiles - 0.001)

                    val newLon = tileXToLon(newTileX, zoom)
                    val newLat = tileYToLat(newTileY, zoom)
                    onCenterChange(newLat, newLon)
                }
            }
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()
        val tilePixelSize = TILE_SIZE * density.density

        val centerTileX = lonToTileXDouble(centerLon, zoom)
        val centerTileY = latToTileYDouble(centerLat, zoom)

        val tilesWide = (widthPx / tilePixelSize).toInt() + 3
        val tilesHigh = (heightPx / tilePixelSize).toInt() + 3

        val startTileX = (floor(centerTileX) - tilesWide / 2).toInt()
        val endTileX = startTileX + tilesWide
        val startTileY = (floor(centerTileY) - tilesHigh / 2).toInt().coerceAtLeast(0)
        val endTileY = (startTileY + tilesHigh).coerceAtMost((1 shl zoom) - 1)

        val maxTiles = 1 shl zoom

        // 1. Render Map Raster Tiles
        Box(modifier = Modifier.fillMaxSize()) {
            for (ty in startTileY..endTileY) {
                for (rawTx in startTileX..endTileX) {
                    val tx = ((rawTx % maxTiles) + maxTiles) % maxTiles
                    val tileUrl = "$tileUrlTemplate/$zoom/$tx/$ty.png"

                    val offsetX = (rawTx - centerTileX) * tilePixelSize + widthPx / 2f
                    val offsetY = (ty - centerTileY) * tilePixelSize + heightPx / 2f

                    if (offsetX + tilePixelSize >= 0 && offsetX <= widthPx &&
                        offsetY + tilePixelSize >= 0 && offsetY <= heightPx
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(tileUrl)
                                .crossfade(true)
                                .addHeader("User-Agent", "AbhayaDisasterApp/1.0")
                                .build(),
                            contentDescription = "Map Tile",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .size(TILE_SIZE.dp)
                                .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
                        )
                    }
                }
            }
        }

        // 2. Vector Layer: Danger Zones & User Pulse Circle
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw Flood Danger Zones
            reports.forEach { report ->
                if (report.category == HazardCategory.FLOOD_WATER || report.category == HazardCategory.WATERLOGGING) {
                    val repTileX = lonToTileXDouble(report.longitude, zoom)
                    val repTileY = latToTileYDouble(report.latitude, zoom)
                    val screenX = (repTileX - centerTileX).toFloat() * tilePixelSize + widthPx / 2f
                    val screenY = (repTileY - centerTileY).toFloat() * tilePixelSize + heightPx / 2f

                    // Radius in pixels approx at this latitude & zoom
                    val metersPerPixel = 156543.03392 * cos(Math.toRadians(report.latitude)) / (1 shl zoom)
                    val radiusPx = (report.radiusMeters / metersPerPixel).toFloat().coerceIn(24f, 260f)

                    // Semi-transparent orange/red circle
                    drawCircle(
                        color = Color(0x44F97316),
                        radius = radiusPx,
                        center = Offset(screenX, screenY)
                    )
                    drawCircle(
                        color = Color(0xAAF97316),
                        radius = radiusPx,
                        center = Offset(screenX, screenY),
                        style = Stroke(width = 3f)
                    )
                }
            }

            // Draw User Pulse Beacon
            val userTileX = lonToTileXDouble(userLon, zoom)
            val userTileY = latToTileYDouble(userLat, zoom)
            val userScreenX = (userTileX - centerTileX).toFloat() * tilePixelSize + widthPx / 2f
            val userScreenY = (userTileY - centerTileY).toFloat() * tilePixelSize + heightPx / 2f

            drawCircle(
                color = Color(0xFF38BDF8).copy(alpha = pulseAlpha),
                radius = pulseRadius * density.density,
                center = Offset(userScreenX, userScreenY)
            )
            drawCircle(
                color = Color(0xFF0284C7),
                radius = 9f * density.density,
                center = Offset(userScreenX, userScreenY)
            )
            drawCircle(
                color = Color.White,
                radius = 4f * density.density,
                center = Offset(userScreenX, userScreenY)
            )
        }

        // 3. Interactive Hazard Marker Badges
        reports.forEach { report ->
            val repTileX = lonToTileXDouble(report.longitude, zoom)
            val repTileY = latToTileYDouble(report.latitude, zoom)
            val markerX = (repTileX - centerTileX).toFloat() * tilePixelSize + widthPx / 2f
            val markerY = (repTileY - centerTileY).toFloat() * tilePixelSize + heightPx / 2f

            if (markerX in -40f..(widthPx + 40f) && markerY in -40f..(heightPx + 40f)) {
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (markerX - 18.dp.toPx()).toInt(),
                                (markerY - 38.dp.toPx()).toInt()
                            )
                        }
                        .clickable { onReportClick(report) }
                ) {
                    HazardMarkerPin(
                        category = report.category,
                        verified = report.verified
                    )
                }
            }
        }

        // 4. Floating Map Controls (Right Side: Zoom +, Zoom -, Layers, Recenter)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Zoom In
            SmallFloatingActionButton(
                onClick = { if (zoom < 18) onZoomChange(zoom + 1) },
                containerColor = SurfaceWhite,
                contentColor = DarkNavy,
                shape = CircleShape,
                modifier = Modifier.size(42.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In")
            }

            // Zoom Out
            SmallFloatingActionButton(
                onClick = { if (zoom > 11) onZoomChange(zoom - 1) },
                containerColor = SurfaceWhite,
                contentColor = DarkNavy,
                shape = CircleShape,
                modifier = Modifier.size(42.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out")
            }

            // Toggle Layer
            SmallFloatingActionButton(
                onClick = { activeTileStyle = if (activeTileStyle == 0) 1 else 0 },
                containerColor = if (activeTileStyle == 1) PrimaryOrange else SurfaceWhite,
                contentColor = if (activeTileStyle == 1) SurfaceWhite else DarkNavy,
                shape = CircleShape,
                modifier = Modifier.size(42.dp)
            ) {
                Icon(Icons.Default.Layers, contentDescription = "Toggle Map Layers")
            }

            // Recenter to live GPS
            SmallFloatingActionButton(
                onClick = onRecenterClick,
                containerColor = SurfaceWhite,
                contentColor = PrimaryOrange,
                shape = CircleShape,
                modifier = Modifier.size(42.dp)
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Recenter to My Location")
            }
        }

        // 5. OpenStreetMap Required Attribution
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 8.dp, bottom = 8.dp)
                .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "© OpenStreetMap contributors",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = DarkNavy
            )
        }
    }
}

@Composable
fun HazardMarkerPin(
    category: HazardCategory,
    verified: Boolean
) {
    val bgColor = when (category) {
        HazardCategory.FLOOD_WATER, HazardCategory.WATERLOGGING -> PrimaryOrange
        HazardCategory.FIRE, HazardCategory.MEDICAL_EMERGENCY -> EmergencyRed
        HazardCategory.OPEN_WIRE, HazardCategory.ROADBLOCK -> AmberAlert
        else -> Color(0xFFD97706)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .shadow(4.dp, shape = CircleShape)
                .size(36.dp)
                .background(bgColor, shape = CircleShape)
                .border(2.dp, if (verified) GreenVerified else SurfaceWhite, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            val icon = when (category) {
                HazardCategory.FLOOD_WATER -> Icons.Default.Water
                HazardCategory.WATERLOGGING -> Icons.Default.WaterDamage
                HazardCategory.FALLEN_TREE -> Icons.Default.Park
                HazardCategory.ROADBLOCK -> Icons.Default.Traffic
                HazardCategory.POTHOLE -> Icons.Default.Warning
                HazardCategory.OPEN_WIRE -> Icons.Default.Bolt
                HazardCategory.LANDSLIDE -> Icons.Default.Landscape
                HazardCategory.FIRE -> Icons.Default.LocalFireDepartment
                HazardCategory.MEDICAL_EMERGENCY -> Icons.Default.LocalHospital
            }
            Icon(
                imageVector = icon,
                contentDescription = category.displayName,
                tint = SurfaceWhite,
                modifier = Modifier.size(20.dp)
            )
        }
        // Small pin point
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(bgColor, CircleShape)
        )
    }
}
