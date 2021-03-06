package internal;


public class HSVconverter {
	//http://www.java2s.com/Code/Java/2D-Graphics-GUI/HSVtoRGB.htm
	public static float[] HSVtoRGB(float h, float s, float v){

	    
		 	float m, n, f;
	        int i;

	        float[] hsv = new float[3];
	        float[] rgb = new float[3];

	        hsv[0] = h;
	        hsv[1] = s;
	        hsv[2] = v;

	        if (hsv[0] == -1)
	        {
	            rgb[0] = rgb[1] = rgb[2] = hsv[2];
	            return rgb;
	        }
	        i = (int) (Math.floor(hsv[0]));
	        f = hsv[0] - i;
	        if (i % 2 == 0)
	        {
	            f = 1 - f; // if i is even
	        }
	        m = hsv[2] * (1 - hsv[1]);
	        n = hsv[2] * (1 - hsv[1] * f);
	        switch (i)
	        {
	            case 0:
	                rgb[0] = hsv[2];
	                rgb[1] = n;
	                rgb[2] = m;
	                break;
	            case 1:
	                rgb[0] = n;
	                rgb[1] = hsv[2];
	                rgb[2] = m;
	                break;
	            case 2:
	                rgb[0] = m;
	                rgb[1] = hsv[2];
	                rgb[2] = n;
	                break;
	            case 3:
	                rgb[0] = m;
	                rgb[1] = n;
	                rgb[2] = hsv[2];
	                break;
	            case 4:
	                rgb[0] = n;
	                rgb[1] = m;
	                rgb[2] = hsv[2];
	                break;
	            case 5:
	                rgb[0] = hsv[2];
	                rgb[1] = m;
	                rgb[2] = n;
	                break;
	        }

	        return rgb;
	}
	//https://www.cs.rit.edu/~ncs/color/t_convert.html
	public static float[] RGBtoHSV(float r, float g, float b){
		
		float[] hsv = new float[3];
        float[] rgb = new float[3];

        rgb[0] = r;
        rgb[1] = g;
        rgb[2] = b;
		float min = Math.min(Math.min(g, b),Math.min(r, g));
		float max = Math.max(Math.max(g, b),Math.max(r, g));
		hsv[2] = max;
		float delta = max - min;
		if(!(max == 0))
			hsv[1] = delta/max;
		else{
			hsv[1] = 0;
			return hsv;
		}
		if(r == max)
			hsv[0] = ((g - b)/delta)%6;			// between yellow & magenta
		else if(g == max)
			hsv[0] = 2 + (b - r) / delta;    // between cyan & yellow
		else if(delta == 0)
			hsv[0] = 0;
		else
			hsv[0] = 4 + (b - r) / delta;	// between magenta & cyan
		
		hsv[0] *= 60;						// degrees
		if(hsv[0]<0)
			hsv[0] += 360;
		return hsv;
	}
	
//		formula source:	http://www.rapidtables.com/convert/color/hsv-to-rgb.htm
		public static float[] HSVtoRGB2(float h, float s, float v){
			float c = s*v;
			float x = c * (1 - (Math.abs((h/60)%2-1)));
			float m = v-c;
			
			float r;
			float g;
			float b;
			
			if (h >= 0 && h < 60){
				r = c;
				g = x;
				b = 0;
			}
			else if (h >= 60 && h < 120){
				r = x;
				g = c;
				b = 0;
			}
			else if (h >= 120 && h < 180){
				r = 0;
				g = c;
				b = x;
			}
			else if (h >= 180 && h < 240){
				r = 0;
				g = x;
				b = c;
			}
			else if (h >= 240 && h < 300){
				r = x;
				g = 0;
				b = c;
			}
			else {
				r = c;
				g = 0;
				b = x;
			}
			float[] rgb = {r+m, g+m, b+m}; 
			return rgb;
		}
}
