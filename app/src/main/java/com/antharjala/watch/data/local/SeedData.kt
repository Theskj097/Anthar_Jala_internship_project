package com.antharjala.watch.data.local

import com.antharjala.watch.data.model.AlertItem
import com.antharjala.watch.data.model.AlertSeverity
import com.antharjala.watch.data.model.BorewellEntry

/**
 * Realistic seed data simulating a groundwater-stressed village in Kolar district, Karnataka.
 * All coordinates are pre-snapped to the 500m grid.
 * Center: approx 13.14°N, 78.13°E
 */
object SeedData {

    val borewellEntries = listOf(
        // Zone A — Northern cluster (moderate stress)
        BorewellEntry(lat = 13.1485, lng = 78.1305, depthFt = 180f, yearDrilled = 2014, yieldInchesPerHour = 2.5f, userId = "demo"),
        BorewellEntry(lat = 13.1485, lng = 78.1305, depthFt = 210f, yearDrilled = 2016, yieldInchesPerHour = 1.8f, userId = "demo"),
        BorewellEntry(lat = 13.1530, lng = 78.1350, depthFt = 195f, yearDrilled = 2015, yieldInchesPerHour = 2.1f, userId = "demo"),
        BorewellEntry(lat = 13.1530, lng = 78.1350, depthFt = 220f, yearDrilled = 2018, yieldInchesPerHour = 1.5f, userId = "demo"),

        // Zone B — Eastern cluster (critical stress)
        BorewellEntry(lat = 13.1440, lng = 78.1440, depthFt = 280f, yearDrilled = 2012, yieldInchesPerHour = 0.5f, userId = "demo"),
        BorewellEntry(lat = 13.1440, lng = 78.1440, depthFt = 300f, yearDrilled = 2019, yieldInchesPerHour = 0.3f, userId = "demo"),
        BorewellEntry(lat = 13.1395, lng = 78.1485, depthFt = 310f, yearDrilled = 2020, yieldInchesPerHour = 0.2f, userId = "demo"),
        BorewellEntry(lat = 13.1395, lng = 78.1485, depthFt = 290f, yearDrilled = 2017, yieldInchesPerHour = 0.8f, userId = "demo"),

        // Zone C — Southern cluster (safe zone)
        BorewellEntry(lat = 13.1350, lng = 78.1260, depthFt = 140f, yearDrilled = 2010, yieldInchesPerHour = 4.0f, userId = "demo"),
        BorewellEntry(lat = 13.1350, lng = 78.1260, depthFt = 155f, yearDrilled = 2013, yieldInchesPerHour = 3.5f, userId = "demo"),
        BorewellEntry(lat = 13.1305, lng = 78.1305, depthFt = 160f, yearDrilled = 2011, yieldInchesPerHour = 3.8f, userId = "demo"),
        BorewellEntry(lat = 13.1305, lng = 78.1305, depthFt = 170f, yearDrilled = 2015, yieldInchesPerHour = 3.2f, userId = "demo"),

        // Zone D — Western cluster (moderate stress)
        BorewellEntry(lat = 13.1440, lng = 78.1170, depthFt = 200f, yearDrilled = 2013, yieldInchesPerHour = 2.0f, userId = "demo"),
        BorewellEntry(lat = 13.1440, lng = 78.1170, depthFt = 240f, yearDrilled = 2018, yieldInchesPerHour = 1.2f, userId = "demo"),
        BorewellEntry(lat = 13.1485, lng = 78.1215, depthFt = 215f, yearDrilled = 2016, yieldInchesPerHour = 1.6f, userId = "demo"),

        // Zone E — Central cluster (critical stress)
        BorewellEntry(lat = 13.1440, lng = 78.1305, depthFt = 260f, yearDrilled = 2011, yieldInchesPerHour = 0.7f, userId = "demo"),
        BorewellEntry(lat = 13.1440, lng = 78.1305, depthFt = 320f, yearDrilled = 2021, yieldInchesPerHour = 0.4f, userId = "demo"),
        BorewellEntry(lat = 13.1395, lng = 78.1350, depthFt = 295f, yearDrilled = 2019, yieldInchesPerHour = 0.6f, userId = "demo"),

        // Zone F — Far south (safe)
        BorewellEntry(lat = 13.1260, lng = 78.1350, depthFt = 130f, yearDrilled = 2009, yieldInchesPerHour = 5.0f, userId = "demo"),
        BorewellEntry(lat = 13.1260, lng = 78.1350, depthFt = 145f, yearDrilled = 2012, yieldInchesPerHour = 4.2f, userId = "demo"),
        BorewellEntry(lat = 13.1215, lng = 78.1395, depthFt = 150f, yearDrilled = 2014, yieldInchesPerHour = 3.9f, userId = "demo"),

        // Zone G — North-East (moderate to critical transition)
        BorewellEntry(lat = 13.1575, lng = 78.1440, depthFt = 255f, yearDrilled = 2017, yieldInchesPerHour = 1.1f, userId = "demo"),
        BorewellEntry(lat = 13.1575, lng = 78.1440, depthFt = 270f, yearDrilled = 2020, yieldInchesPerHour = 0.9f, userId = "demo"),
    )

    val alerts = listOf(
        AlertItem(
            zoneName = "Zone B (East)",
            message = "⚠️ Water table in Zone B has dropped by 12 ft this summer. Yield below 1 in/hr. Consider stopping new borewells and starting recharge activities.",
            severity = AlertSeverity.CRITICAL,
            timestamp = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000L
        ),
        AlertItem(
            zoneName = "Zone E (Central)",
            message = "⚠️ Average borewell depth in Zone E increased by 30 ft over 2 years. Groundwater depletion trend detected.",
            severity = AlertSeverity.WARNING,
            timestamp = System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000L
        ),
        AlertItem(
            zoneName = "Zone G (North-East)",
            message = "ℹ️ Zone G is approaching moderate stress. Current average yield: 1.0 in/hr. Monitor closely before summer peak.",
            severity = AlertSeverity.WARNING,
            timestamp = System.currentTimeMillis() - 8 * 24 * 60 * 60 * 1000L
        ),
        AlertItem(
            zoneName = "Zone C (South)",
            message = "✅ Zone C remains in safe status. Average yield: 3.6 in/hr. Recharge structures in this area are showing positive results.",
            severity = AlertSeverity.INFO,
            timestamp = System.currentTimeMillis() - 12 * 24 * 60 * 60 * 1000L
        ),
        AlertItem(
            zoneName = "Village-Wide",
            message = "📢 Community Notice: Pre-monsoon borewell recharge season begins. Please log your current yield to help track village water health.",
            severity = AlertSeverity.INFO,
            timestamp = System.currentTimeMillis() - 15 * 24 * 60 * 60 * 1000L
        )
    )
}
