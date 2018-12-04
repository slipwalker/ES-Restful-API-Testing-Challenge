package com.technicaltask;

import ru.qatools.properties.Property;
import ru.qatools.properties.PropertyLoader;
import ru.qatools.properties.Resource;

@Resource.Classpath("default.properties")
public interface DefaultProperties {

    @Property("es.docker.image")
    String ES_DOCKER_IMAGE();

    DefaultProperties PROPERTIES = PropertyLoader.newInstance().populate(DefaultProperties.class);

    static String getEsDockerImage() {
        return PROPERTIES.ES_DOCKER_IMAGE();
    }
}