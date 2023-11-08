package ru.ryazancev.testutils.paths;

public class APIPathBuilder {
    private final StringBuilder pathBuilder = new StringBuilder();

    private APIPathBuilder() {
    }

    public static APIPathBuilder builder() {
        return new APIPathBuilder();
    }

    public APIPathBuilder withAdmin(){
        pathBuilder.append("/admin");
        return this;
    }

    public APIPathBuilder withBase(APIBase base) {
        pathBuilder.append(base.getValue());
        return this;
    }

    public APIPathBuilder withPath(PathParts path) {
        pathBuilder.append("/").append(path.getValue());
        return this;
    }

    public APIPathBuilder withId() {
        pathBuilder.append("/{id}");
        return this;
    }

    public String build() {
        return pathBuilder.toString();
    }
}
