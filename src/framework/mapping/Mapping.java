package framework.mapping;

public class Mapping {
    String controllerName;
    String methodName;

    public Mapping(String controllerName, String methodName) {
        this.controllerName = controllerName;
        this.methodName = methodName;
    }

    public String getControllerName() {
        return controllerName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
