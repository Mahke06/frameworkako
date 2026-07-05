package framework.mapping;

import java.util.Objects;

public class VerbUrl {
    private String url;
    private String method;

    public VerbUrl(String url, String method) {
        this.url = url;
        this.method = method.toUpperCase();
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        VerbUrl verbUrl = (VerbUrl) obj;
        return Objects.equals(url, verbUrl.url) && Objects.equals(method, verbUrl.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, method);
    }
}
