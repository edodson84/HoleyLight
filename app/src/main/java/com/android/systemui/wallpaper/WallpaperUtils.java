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

package com.android.systemui.wallpaper;

// This class is needed to prevent a crash on S10 when calling getFaceRecognitionVIFileName()
@SuppressWarnings({"SameReturnValue", "unused"})
public class WallpaperUtils {
    public static boolean isWhiteKeyguardWallpaper(String s) {
        return false;
    }
}
