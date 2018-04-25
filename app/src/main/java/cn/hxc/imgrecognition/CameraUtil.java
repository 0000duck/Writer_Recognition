package cn.hxc.imgrecognition;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.view.Surface;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraUtil {
	static CameraUtil cameraUtil = null;

	public static CameraUtil getInstance() {
		if (cameraUtil == null) {
			cameraUtil = new CameraUtil();
			return cameraUtil;
		} else {
			return cameraUtil;
		}
	}

	private CameraSizeComparator sizeComparator = new CameraSizeComparator();
	public Size getPictureSizeSort(List<Size> sizes) {
		Size size = sizes.get(0);
		for (int i = 0; i < sizes.size(); i++) {
			if (sizes.get(i).height > size.height)
				size = sizes.get(i);
		}
		return size;
	}

	public Size getPictureSize(List<Size> list, int th) {
		Collections.sort(list, sizeComparator);

		int i = 0;
		for (Size s : list) {
			if ((s.width > th) && equalRate(s, 1.33f)) {
				break;
			}
			i++;
		}
		return list.get(i);
	}

	public boolean equalRate(Size s, float rate) {
		float r = (float) (s.width) / (float) (s.height);
		if (Math.abs(r - rate) <= 0.2) {
			return true;
		} else {
			return false;
		}
	}

	public class CameraSizeComparator implements Comparator<Size> {
		//
		public int compare(Size lhs, Size rhs) {
			// TODO Auto-generated method stub
			if ((lhs.width > rhs.width) && (lhs.height >= rhs.height))
				return -1;
			else if ((rhs.width > lhs.width) && (rhs.height >= lhs.height))
				return 1;
			else if ((lhs.width == rhs.width)) {
				if (lhs.height > rhs.height)
					return -1;
				else if (rhs.height > lhs.height)
					return 1;
				else
					return 0;
			} else
				return 0;

		}
	}

	public Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.05;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;
		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;
		int targetHeight = h;
		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}
		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	public int setCameraDisplayOrientation(Activity activity, int cameraId,
			Camera camera) {
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;// compensate the mirror
		} else {// back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		// displayOrientation = result;
		// camera.setDisplayOrientation(result);
		return result;
	}

	/* *
	 * �Ƿ����������
	 * 
	 * @return
	 */
	public boolean isFlashlightOn(Camera camera) {
		try {
			Camera.Parameters parameters = camera.getParameters();
			String flashMode = parameters.getFlashMode();
			if (flashMode
					.equals(Camera.Parameters.FLASH_MODE_TORCH)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

}
