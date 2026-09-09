package com.flux.tuner;

public final class FluxTuner {

    public enum Profile {
        MAXIMUM_FPS,
        STABLE_FPS,
        BALANCED
    }

    public enum Status {
        NOT_DETECTED,
        READY,
        APPLIED
    }

    private Profile selectedProfile = Profile.BALANCED;
    private Status status = Status.NOT_DETECTED;

    public void detect() {
        /*
         * Sodium integration will be connected here.
         *
         * We intentionally keep detection separate from
         * the profile logic so unsupported Sodium versions
         * can fail safely.
         */
        status = Status.NOT_DETECTED;
    }

    public void setProfile(Profile profile) {
        if (profile == null) {
            return;
        }

        selectedProfile = profile;
    }

    public Profile getSelectedProfile() {
        return selectedProfile;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isAvailable() {
        return status != Status.NOT_DETECTED;
    }

    public void apply() {
        if (!isAvailable()) {
            return;
        }

        /*
         * Actual Sodium settings will be applied here.
         * Before changing anything, the previous values
         * must be stored for Restore.
         */

        status = Status.APPLIED;
    }

    public void restore() {
        /*
         * Previous Sodium settings will be restored here.
         */

        status = Status.READY;
    }
}
