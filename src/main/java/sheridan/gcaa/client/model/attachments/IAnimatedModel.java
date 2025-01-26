package sheridan.gcaa.client.model.attachments;

import sheridan.gcaa.client.animation.frameAnimation.AnimationDefinition;

public interface IAnimatedModel {
    AnimationDefinition getAnimation(String name);
}
