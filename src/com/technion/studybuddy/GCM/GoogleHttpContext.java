package com.technion.studybuddy.GCM;

import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;

/* 
 *  usage example (string ServerName,Account account,Activity this_)
 *  	googleHttpContext = new GoogleHttpContextProduction(this_,
 account.name,
 ServerName);
 *  	DefaultHttpClient client = new DefaultHttpClient();
 HttpGet get = new HttpGet("http://"+ServerName+"/test");
 HttpResponse response;
 try
 {
 response = client.execute(get,googleHttpContext);
 ByteArrayOutputStream out = new ByteArrayOutputStream();
 response.getEntity().writeTo(out);
 out.close();
 final String content = out.toString();

 } catch (ClientProtocolException e)
 {
 e.printStackTrace();
 } catch (IOException e)
 {
 e.printStackTrace();
 }
 */
public abstract class GoogleHttpContext implements HttpContext
{
	protected Context context;
	protected HttpContext httpContext;
	protected GoogleHttpContext this_;
	public static final int USER_PERMISSION = 999;

	/**
	 * @param context
	 *            - activity type context for account manager and activity start
	 */
	public GoogleHttpContext(Context context)
	{
		super();
		this.context = context;
		this_ = this;
		httpContext = new BasicHttpContext();
	}

}
