package com.bumptech.glide.integration.avif;


import com.bumptech.glide.load.Option;

public class AvifOptions {
    public static final Option<Boolean> DISABLE_ANIMATION =
            Option.memory("com.bumptech.glide.integration.avif.decoder.DisableAnimation", false);

    public static final Option<Boolean> LOOP_ONCE =
            Option.memory("com.bumptech.glide.integration.avif.LoopOnce", false);
}
