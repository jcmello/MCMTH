package gpdi.py;

import java.io.File;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageConverter;

public class Test_MCMTH {

	public static void main(String[] args) {
		String pathBase=System.getProperty("user.dir")+File.separator+"Mineralogy_Database"+File.separator;
        String path = pathBase+"68.png";
        ImagePlus imp = IJ.openImage(path);
        ImageConverter ic = new ImageConverter(imp);
		ic.convertToGray8();
		imp.updateAndDraw();	
        ImagePlus imp2 = imp.duplicate();
		double mcmth = MCMTH.mcmTH(imp2);
        System.out.println(mcmth);
	}

}
