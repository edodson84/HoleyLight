/*
 * Copyright (C) 2019-2022 Jorrit "Chainfire" Jongma
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package eu.chainfire.holeylight.service.area;

import android.content.Context;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import eu.chainfire.holeylight.misc.Settings;
import eu.chainfire.holeylight.misc.Slog;

public class AreaFinderSamsung13 extends AreaFinder {
    private Rect clockArea = null;
    private Integer overlayBottom = null;

    private void logNode(int level, AccessibilityNodeInfo node, Rect bounds) {
        if (Settings.DEBUG) {
            StringBuilder sb = new StringBuilder("");
            for (int i = 0; i < level; i++) {
                sb.append("--");
            }
            if (sb.length() > 0) sb.append(" ");
            Slog.d(TAG, "Node " + sb.toString() + node.getClassName().toString() + " " + bounds.toString() + " " + node.getViewIdResourceName());
        }
    }

    private void inspectNode(AccessibilityNodeInfo node, Rect outerBounds, int level) {
        if (
                (node == null) ||
                (node.getClassName() == null) ||
                (!Settings.DEBUG && (
                        (!node.getClassName().equals("android.widget.FrameLayout")) &&
                        (!node.getClassName().equals("android.widget.LinearLayout")) &&
                        (!node.getClassName().equals("android.widget.RelativeLayout")) &&
                        (!"com.samsung.android.app.aodservice:id/common_battery_text".equals(node.getViewIdResourceName()))
                ))
        ) {
            return;
        }

        node.refresh();

        Rect bounds = new Rect();
        node.getBoundsInScreen(bounds);
        logNode(level, node, bounds);

        if ("com.samsung.android.app.aodservice:id/common_battery_text".equals(node.getViewIdResourceName())) {
            overlayBottom = bounds.top - 1;
            Slog.d(TAG, "|||||||||||||||||||||||||||||||||||||||||||||||||||");
        } else if ("com.samsung.android.app.aodservice:id/common_image_time_date".equals(node.getViewIdResourceName())) {
            clockArea = bounds;
            Slog.d(TAG, "+++++++++++++++++++++++++++++++++++++++++++++++++++");
        } else if (
                "com.samsung.android.app.aodservice:id/common_clock_widget_container".equals(node.getViewIdResourceName()) ||
                "com.samsung.android.app.aodservice:id/keyguard_aod_ui_container".equals(node.getViewIdResourceName())
        ) {
            if ((bounds.left >= 0) && (bounds.right >= 0) && ((outerBounds.left == -1) || (bounds.left < outerBounds.left))) outerBounds.left = bounds.left;
            if ((bounds.top >= 0) && (bounds.bottom >= 0) && ((outerBounds.top == -1) || (bounds.top < outerBounds.top))) outerBounds.top = bounds.top;
            if ((bounds.left >= 0) && (bounds.right >= 0) && ((outerBounds.right == -1) || (bounds.right > outerBounds.right))) outerBounds.right = bounds.right;
            if ((bounds.top >= 0) && (bounds.bottom >= 0) && ((outerBounds.bottom == -1) || (bounds.bottom > outerBounds.bottom))) outerBounds.bottom = bounds.bottom;
            Slog.d(TAG, "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        }

        if (
                node.getClassName().equals("android.widget.FrameLayout") ||
                node.getClassName().equals("android.widget.LinearLayout") ||
                node.getClassName().equals("android.widget.RelativeLayout") ||
                Settings.DEBUG
        )  {
            for (int i = 0; i < node.getChildCount(); i++) {
                inspectNode(node.getChild(i), outerBounds, level + 1);
            }
        }
    }

    @Override
    public void start(Context context) {
        overlayBottom = null;
        clockArea = null;
    }

    @Override
    public Rect find(AccessibilityNodeInfo root) {
        Rect outerBounds = new Rect(-1, -1, -1, -1);

        inspectNode(root, outerBounds, 0);
        if ((clockArea != null) && (clockArea.left >= 0) && (outerBounds.left >= 0) && (clockArea.left > outerBounds.left))
            outerBounds.left = clockArea.left;
        if ((clockArea != null) && (clockArea.right >= 0) && (outerBounds.right >= 0) && (clockArea.right < outerBounds.right))
            outerBounds.right = clockArea.right;

        return outerBounds;
    }

    @Override
    public Rect findClock(AccessibilityNodeInfo root) {
        if ((clockArea != null) && (clockArea.top >= 0) && (clockArea.left >= 0) && (clockArea.width() > 0) && (clockArea.height() > 0)) return clockArea;
        return null;
    }

    @Override
    public Integer findOverlayBottom(AccessibilityNodeInfo root) {
        return overlayBottom;
    }
}
