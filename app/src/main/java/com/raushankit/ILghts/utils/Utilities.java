package com.raushankit.ILghts.utils;

import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

public class Utilities {

    public static String debugInstallStatus(int code) {
        String dbg;
        switch (code) {
            case InstallStatus.INSTALLED:
                dbg = "InstallStatus.INSTALLED";
                break;
            case InstallStatus.DOWNLOADED:
                dbg = "InstallStatus.DOWNLOADED";
                break;
            case InstallStatus.DOWNLOADING:
                dbg = "InstallStatus.DOWNLOADING";
                break;
            case InstallStatus.CANCELED:
                dbg = "InstallStatus.CANCELED";
                break;
            case InstallStatus.FAILED:
                dbg = "InstallStatus.FAILED";
                break;
            case InstallStatus.PENDING:
                dbg = "InstallStatus.PENDING";
                break;
            case InstallStatus.UNKNOWN:
                dbg = "InstallStatus.UNKNOWN";
                break;
            default:
                dbg = "purely unknown";
        }
        return dbg;
    }

    public static String debugUpdateAvailability(int code) {
        String dbg;
        switch (code) {
            case UpdateAvailability.UPDATE_AVAILABLE:
                dbg = "UpdateAvailability.UPDATE_AVAILABLE";
                break;
            case UpdateAvailability.UPDATE_NOT_AVAILABLE:
                dbg = "UpdateAvailability.UPDATE_NOT_AVAILABLE";
                break;
            case UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS:
                dbg = "UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS";
                break;
            case UpdateAvailability.UNKNOWN:
                dbg = "UpdateAvailability.UNKNOWN";
                break;
            default:
                dbg = "purely unknown";
        }
        return dbg;
    }
}
