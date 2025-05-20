#version 150

#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler1;
uniform sampler2D Sampler2;
uniform ivec2 GlobalLightMapUV;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat3 IViewRotMat;
uniform int FogShape;

uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;

out float vertexDistance;
out vec4 vertexColor;
out vec4 lightMapColor;
out vec4 overlayColor;
out vec2 texCoord0;
out vec4 normal;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    vertexDistance = fog_distance(ModelViewMat, IViewRotMat * Position, FogShape);

    mat3 normalMatrix = mat3(transpose(inverse(ModelViewMat)));
    vec3 transformedNormal = normalize(normalMatrix * Normal);

    vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, transformedNormal, Color);

    lightMapColor = texelFetch(Sampler2, GlobalLightMapUV, 0);
    overlayColor = texelFetch(Sampler1, UV1, 0);
    texCoord0 = UV0;

    normal = ProjMat * ModelViewMat * vec4(transformedNormal, 0.0);
}