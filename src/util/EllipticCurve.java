package util;

import java.util.ArrayList;

public class EllipticCurve {

	public static long NEUTRAL_VALUE = Long.MAX_VALUE;
	public static long DEFAULT_A = 15485867L;
	public static long DEFAULT_B = -32452867L;
	public static long DEFAULT_P = 198491317L;
	
	private long a,b,p; // elliptic curve's parameters
	private long[] basePoint; // basePoint[0] = x, basePoint[1] = y
	private ArrayList<Long> GF;
	
	/**
	 * constructor without base point
	 * @param a
	 * @param b
	 * @param p
	 */
	public EllipticCurve(long a, long b, long p) {
		this.a = a;
		this.b = b;
		this.p = p;
	}
	
	/**
	 * constructor with defined base point
	 * @param |a| < p
	 * @param |b| < p
	 * @param p : prime number
	 * @param xb
	 * @param yb <> 0
	 */
	public EllipticCurve(long a, long b, long p, long xb, long yb) {
		this.a = a;
		this.b = b;
		this.p = p;
		
		basePoint = new long[2];
		basePoint[0] = xb;
		basePoint[1] = yb;
	}
	
	public long getA() {
		return a;
	}
	
	public long getB() {
		return b;
	}
	
	public long getP() {
		return p;
	}
	
	public long[] getBasePoint() {
		return basePoint;
	}
	
	public void setA(long a) {
		this.a = a;
	}
	
	public void setB(long b) {
		this.b = b;
	}
	
	public void setP(long p) {
		if (Prime.isPrime(p)) {
			this.p = p;
		}
	}
	
	public void setBasePoint(long x, long y) {
		if (basePoint == null) basePoint = new long[2];
		basePoint[0] = x;
		basePoint[1] = y;
	}
	
	private void generateGaloisField() {
		if (GF != null) GF = null;
		GF = new ArrayList<Long>();
		
		for (long l = 0; l < Math.min(1000000L, p/2); l++) {
			GF.add((l*l) % p);
		}
	}
	
	public void generateBasePoint() {
		generateGaloisField();
		long x = p / 2;
		long y = getY(x);
		while (y == 0L) {
			x--;
			y = getY(x);
		}
		
		setBasePoint(x, y);
	}
	
	public static boolean isValidParameter(long a, long b) {
		return (4*a*a*a + 27*b*b != 0L);
	}
	
	public boolean isValidPoint(long x, long y) {
		long LHS = (y*y) % p;
		long RHS = ((x * x) % p + a) % p;
		RHS *= x;
		RHS %= p;
		RHS += b;
		
		return LHS == RHS;
	}
	
	public boolean isValid(long x){
		//Apakah nilai X sudah memenuhi persamaan kurva eliptik?
		long persamaan2 = (x * x * x + a * x + b);
		//System.out.println("Px: "+persamaan2);

		persamaan2 = persamaan2 % p;

		double akar = Math.sqrt(persamaan2);
		if (Math.round(akar) == akar) return true;
		else return false;
	}
	
	public long getY(long x){
		//Apakah nilai X sudah memenuhi persamaan kurva eliptik?
		long persamaan2 = ((x * x) % p + a) % p;
		persamaan2 *= x;
		persamaan2 %= p;
		persamaan2 += b;

		if (GF.contains(persamaan2)) return GF.indexOf(persamaan2);
		return 0L;
	}
	
	/**
	 * calculate 2P
	 * @return
	 */
	public long[] doublePoint(long[] P) {
		long[] retval = new long[2];
		
		long lambda = (((3*P[0]*P[0]) % p) + a) % p;
		long inv = Prime.getInverse(2*P[1], p);
		lambda *= inv;
		lambda %= p;
		
		retval[0] = (((lambda*lambda) % p) - (2*P[0] % p) + p) % p;
		retval[1] = (((lambda*(P[0] - retval[0])) % p) - P[1] + p) % p;
		
		return retval;
	}
	
	/**
	 * calculate P + Q
	 * @param P
	 * @param Q
	 * @return
	 */
	public long[] addPoint(long[] P, long[] Q) {		
		long[] retval = new long[2];
		
		if (P[0] == NEUTRAL_VALUE) {
			retval[0] = Q[0];
			retval[1] = Q[1];
		} else if (Q[0] == NEUTRAL_VALUE) {
			retval[0] = P[0];
			retval[1] = P[1];
		} else {
			long lambda = (P[1] - Q[1] + p) % p;
			long inv = Prime.getInverse(P[0] - Q[0], p);
			lambda *= inv;
			lambda %= p;
			
			retval[0] = (((lambda*lambda) % p) - P[0] - Q[0] + 2*p) % p;
			retval[1] = (((lambda*(P[0] - retval[0])) % p) - P[1] + p) % p;
		}
		
		return retval;
	}
	
	public long[] getPublicKey(long privateKey) {
		return multiplyPoint(privateKey, basePoint);
	}
	
	public long[] multiplyPoint(long k, long[] P) {
		long[] retval = new long[2];
		retval[0] = NEUTRAL_VALUE;
		retval[1] = NEUTRAL_VALUE;
		
		long[] base = new long[2];
		base[0] = P[0];
		base[1] = P[1];
		
		String binary = Long.toBinaryString(k);
		for (int i = binary.length()-1; i >= 0; i--) {
			if (binary.charAt(i) == '1') {
				retval = addPoint(retval, base);
			}
			base = doublePoint(base);
		}
		
		return retval;
	}
}
