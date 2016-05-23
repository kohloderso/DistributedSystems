package hw9.server;

import java.math.BigInteger;


import javax.jws.WebService;

@WebService(endpointInterface = "hw9.server.Comp")
public class CompImpl implements Comp{

	@Override
	public int add(int a, int b) {
		return a+b;
	}

	@Override
	public int sub(int a, int b) {
		return a-b;
	}

	@Override
	public int mul(int a, int b) {
		return a*b;
	}

	@Override
	public BigInteger lucas(long x) {
        return lucasTailRec(new BigInteger("2"),new BigInteger("1"), x);
    }

	private static BigInteger lucasTailRec(final BigInteger a, final BigInteger b, final long n)
    {
        return n < 1 ? a : n == 1 ?  b : lucasTailRec(b, a.add(b), n - 1);
    }

}