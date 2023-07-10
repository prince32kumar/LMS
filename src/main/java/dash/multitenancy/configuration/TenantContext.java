package dash.multitenancy.configuration;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class TenantContext {

	public static final String NO_TENANT = "NO_TENANT";
	public static final String TENANT_NOT_FOUND = "TENANT_NOT_FOUND";
	public static final String PUBLIC_TENANT = "public";
	public static final String TENANT_KEY = "tenant";

	private static ThreadLocal<String> threadLocalTenantKey = new ThreadLocal<>();

	public static String getTenant() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		String tenantId = null;
		if (requestAttributes != null) {
			tenantId = (String) requestAttributes.getAttribute(TENANT_KEY, RequestAttributes.SCOPE_REQUEST);
		}
		if (tenantId != null) {
			return tenantId;
		} else if (threadLocalTenantKey.get() != null) {
			return threadLocalTenantKey.get();
		}
		return PUBLIC_TENANT;
	}

	public static void setTenant(String tenantCode) {

		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

		if (requestAttributes != null) {
			requestAttributes.setAttribute(TENANT_KEY, tenantCode, RequestAttributes.SCOPE_REQUEST);
			RequestContextHolder.setRequestAttributes(requestAttributes);
		} else {
			threadLocalTenantKey.set(tenantCode);
		}

	}
}
