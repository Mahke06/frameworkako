package framework.mapping;

public class Mapping {
    String ControllerName;
    String methodName;

    public Mapping(String controllerName, String methodName) {
        this.ControllerName = controllerName;
        this.methodName = methodName;
    }

    public String getControllerName() {
        return ControllerName;
    }
    public String getMethodName() {
        return methodName;
    }
    public void setControllerName(String controllerName) {
        this.ControllerName = controllerName;
    }
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}