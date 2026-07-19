package framework.mapping;

public class Mapping {
    private String controllerName;
    private String methodName;

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
}
