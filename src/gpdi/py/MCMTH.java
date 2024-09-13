package gpdi.py;

import java.math.BigDecimal;
import java.math.RoundingMode;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import inra.ijpb.morphology.Morphology;
import inra.ijpb.morphology.Reconstruction;
import inra.ijpb.morphology.Strel;
import inra.ijpb.morphology.strel.DiskStrel;

public class MCMTH {

	public static double mcmTH(ImagePlus im) {
		double mcm = 0.0;
		int n = 3;
		int eeTam = 1;
		ImageProcessor FRWI = im.duplicate().getProcessor();
		ImageProcessor FRBI = im.duplicate().getProcessor();
		ImageProcessor CFIE = im.duplicate().getProcessor();
		for (int i = 1; i <= n; i++) {
			ImageProcessor ip = im.duplicate().getProcessor();
			ImageProcessor imB = im.duplicate().getProcessor();
			ImageProcessor imW = im.duplicate().getProcessor();
			ImageProcessor wth = im.duplicate().getProcessor();
			ImageProcessor bth = im.duplicate().getProcessor();

			Strel H = DiskStrel.fromRadius(eeTam);
			// Strel H = LinearDiagDownStrel.fromRadius(eeTam);
			// Strel H = LinearVerticalStrel.fromRadius(eeTam);
			// Strel H = LinearHorizontalStrel.fromRadius(eeTam);
			wth = Morphology.whiteTopHat(ip, H);
			bth = Morphology.blackTopHat(ip, H);

			// ------------------------------------------
			ImageProcessor eRosion = Morphology.erosion(ip, H);
			ImageProcessor aReconst = Reconstruction.reconstructByDilation(eRosion, ip, 8); 
			imW = resta(ip, aReconst);
			ImageProcessor dIlation = Morphology.dilation(ip, H);
			ImageProcessor cReconst = Reconstruction.reconstructByErosion(dIlation, ip, 8);
			imB = resta(cReconst, ip);
			imW = Reconstruction.reconstructByDilation(imW, wth, 8);
			imB = Reconstruction.reconstructByDilation(imB, bth, 8);

			if (i == 1) {
				FRWI = imW.duplicate();
				FRBI = imB.duplicate();
			} else {
				int ancho = im.getWidth();
				int largo = im.getHeight();
				ImageProcessor iwp = imW;
				ImageProcessor ibp = imB;

				for (int x = 0; x < ancho; x++) {
					for (int y = 0; y < largo; y++) {
						FRWI.set(x, y, (int) FRWI.getPixel(x, y) + (int) iwp.getPixel(x, y));
						FRBI.set(x, y, (int) FRBI.getPixel(x, y) + (int) ibp.getPixel(x, y));
					}
				}
			}
			eeTam = eeTam + 1;
		}
		if (FRWI.getStatistics().max > FRBI.getStatistics().max) {
			CFIE = FRWI.duplicate();
		} else {
			CFIE = FRBI.duplicate();
		}
		mcm = CFIE.getStatistics().mean;

		return new BigDecimal(mcm).setScale(3, RoundingMode.HALF_UP).doubleValue();
	}

	private static ImageProcessor resta(ImageProcessor f1, ImageProcessor f2) {
		int M = f1.getWidth();
		int N = f1.getHeight();
		ImageProcessor res = f1.createProcessor(M, N);
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				int val = f1.getPixel(i, j) - f2.getPixel(i, j);
				if (val < 0)
					val = 0;
				res.putPixel(i, j, val);
			}
		}
		return res;
	}
}
