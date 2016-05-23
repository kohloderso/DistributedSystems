package hw9.server;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import java.math.*;


@WebService
@SOAPBinding(style = Style.RPC)
public interface Comp {

	@WebMethod int add(int a, int b);
	@WebMethod int sub(int a, int b);
	@WebMethod int mul(int a, int b);
	@WebMethod BigInteger lucas(long n) ;
}