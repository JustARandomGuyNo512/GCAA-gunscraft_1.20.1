package sheridan.gcaa.client.model.modelPart;

import org.joml.Vector3f;

import java.util.stream.Stream;

public interface IAnimatedModelPart {
    Stream<IAnimatedModelPart> getAllParts();

    boolean hasChild(String pName);

    IAnimatedModelPart getChild(String pName);

    void offsetPos(Vector3f vector3f);

    void offsetRotation(Vector3f vector3f);

    void offsetScale(Vector3f vector3f);
}
