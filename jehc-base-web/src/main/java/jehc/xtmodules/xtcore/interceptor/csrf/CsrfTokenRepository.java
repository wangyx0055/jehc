package jehc.xtmodules.xtcore.interceptor.csrf;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * spring security csrfTokenRepository
 * @author Administrator
 *
 */
public interface CsrfTokenRepository {
    /**
     * Generates a {@link CsrfTokenBean}
     *
     * @param request the {@link HttpServletRequest} to use
     * @return the {@link CsrfTokenBean} that was generated. Cannot be null.
     */
    CsrfTokenBean generateToken(HttpServletRequest request);

    /**
     * Saves the {@link CsrfTokenBean} using the {@link HttpServletRequest} and
     * {@link HttpServletResponse}. If the {@link CsrfTokenBean} is null, it is the same as
     * deleting it.
     *
     * @param token the {@link CsrfTokenBean} to save or null to delete
     * @param request the {@link HttpServletRequest} to use
     * @param response the {@link HttpServletResponse} to use
     */
    void saveToken(CsrfTokenBean token, HttpServletRequest request,
            HttpServletResponse response);

    /**
     * Loads the expected {@link CsrfTokenBean} from the {@link HttpServletRequest}
     *
     * @param request the {@link HttpServletRequest} to use
     * @return the {@link CsrfTokenBean} or null if none exists
     */
    CsrfTokenBean loadToken(HttpServletRequest request);

    /**
     * 缓存来源的url
     * @param request request the {@link HttpServletRequest} to use
     * @param response the {@link HttpServletResponse} to use
     */
    void cacheUrl(HttpServletRequest request, HttpServletResponse response);

    /**
     * 获取并清理来源的url
     * @param request the {@link HttpServletRequest} to use
     * @param response the {@link HttpServletResponse} to use
     * @return 来源url
     */
    String getRemoveCacheUrl(HttpServletRequest request, HttpServletResponse response);
}
