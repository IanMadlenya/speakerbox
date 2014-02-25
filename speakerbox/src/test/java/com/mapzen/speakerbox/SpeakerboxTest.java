package com.mapzen.speakerbox;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.reflect.core.Reflection.field;
import static org.robolectric.Robolectric.application;

@RunWith(SpeakerboxTestRunner.class)
public class SpeakerboxTest {
    private Activity activity;
    private Speakerbox speakerbox;
    private ShadowTextToSpeech shadowTextToSpeech;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(Activity.class).create().start().resume().get();
        speakerbox = new Speakerbox(activity);
        shadowTextToSpeech = Robolectric.shadowOf_(speakerbox.textToSpeech);
    }

    @Test
    public void shouldNotBeNull() throws Exception {
        assertThat(speakerbox).isNotNull();
    }

    @Test
    public void shouldInitTextToSpeech() throws Exception {
        assertThat(shadowTextToSpeech.getContext()).isEqualTo(activity);
        assertThat(shadowTextToSpeech.getOnInitListener()).isEqualTo(speakerbox);
    }

    @Test
    public void shouldSpeakText() throws Exception {
        speakerbox.play("Hello");
        assertThat(shadowTextToSpeech.getLastSpokenText()).isEqualTo("Hello");
    }

    @Test
    public void shouldShutdownTextToSpeechOnActivityDestroyed() throws Exception {
        speakerbox.callbacks.onActivityDestroyed(activity);
        assertThat(shadowTextToSpeech.isShutdown()).isTrue();
    }

    @Test
    public void shouldNotShutdownTextToSpeechOnAnotherActivityDestroyed() throws Exception {
        speakerbox.callbacks.onActivityDestroyed(new Activity());
        assertThat(shadowTextToSpeech.isShutdown()).isFalse();
    }

    @Test
    public void shouldUnregisterLifecycleCallbacksOnActivityDestroyed() throws Exception {
        speakerbox.callbacks.onActivityDestroyed(activity);
        ArrayList<Application.ActivityLifecycleCallbacks> callbackList =
                field("mActivityLifecycleCallbacks").ofType(ArrayList.class).in(application).get();
        assertThat(callbackList).isEmpty();
    }
}
