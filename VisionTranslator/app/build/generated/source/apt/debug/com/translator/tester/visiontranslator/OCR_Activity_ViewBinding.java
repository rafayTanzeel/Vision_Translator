// Generated code from Butter Knife. Do not modify!
package com.translator.tester.visiontranslator;

import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.translator.tester.visiontranslator.camera.CameraScreen;
import com.translator.tester.visiontranslator.camera.GraphicOverlay;
import java.lang.IllegalStateException;
import java.lang.Override;

public final class OCR_Activity_ViewBinding implements Unbinder {
  private OCR_Activity target;

  @UiThread
  public OCR_Activity_ViewBinding(OCR_Activity target, View source) {
    this.target = target;

    target.cameraScreen = Utils.findRequiredViewAsType(source, R.id.preview, "field 'cameraScreen'", CameraScreen.class);
    target.ocrGraphicOverlay = Utils.findRequiredViewAsType(source, R.id.graphicOverlay, "field 'ocrGraphicOverlay'", GraphicOverlay.class);
    target.fab = Utils.findRequiredViewAsType(source, R.id.fab, "field 'fab'", FloatingActionButton.class);
  }

  @Override
  public void unbind() {
    OCR_Activity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.cameraScreen = null;
    target.ocrGraphicOverlay = null;
    target.fab = null;

    this.target = null;
  }
}
