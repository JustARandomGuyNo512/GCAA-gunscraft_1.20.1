#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;

uniform mat4 ProjMat;
uniform vec2 OutSize;

uniform mat4 InversePerspectiveProjMat;
uniform mat4 InverseModelViewMat;
uniform vec3 To;
uniform float Angle;
uniform float Range;
uniform float Luminance;
uniform float MinZ;
uniform int Mode;

in vec2 texCoord;
out vec4 fragColor;

vec3 From = vec3(0.0, 0.0, 0.0);

vec3 getFragWorldPos(vec2 coord) {
    float depth = texture(DiffuseDepthSampler, coord).r;
    vec4 clipPos = vec4(coord * 2.0 - 1.0, depth * 2.0 - 1.0, 1.0);
    vec4 viewPos = InversePerspectiveProjMat * clipPos;
    viewPos /= viewPos.w;
    return vec3(InverseModelViewMat * viewPos);
}

void main(){
    vec4 diffuseColor = texture(DiffuseSampler, texCoord);
    float depth = texture(DiffuseDepthSampler, texCoord).r;
    vec3 pos = getFragWorldPos(texCoord);
    vec3 lightDir = pos - From;
    float dist = length(lightDir);
    if (dist > Range || depth < MinZ) {
      fragColor = vec4(diffuseColor.rgb, 1.0);
        return;
    }
    lightDir = normalize(lightDir);
    vec3 lightTo = normalize(To - From);
    float angleCos = dot(lightDir, lightTo);
    float cutoffCos = cos(Angle);
    float intensity = 0.0;
    float disToCenter = sqrt(1.0 - angleCos * angleCos);
    if (Mode == 1) {
        intensity = clamp(exp(- disToCenter * 15) * Luminance / (dist * 0.015) * (Range - dist) / Range, 0.0, 1.8);
    } else if (Mode == 2) {
        if (angleCos > cutoffCos) {
            intensity += smoothstep(cutoffCos, 1.0, angleCos) * (1.0 - dist / Range) * clamp(Luminance, 0.0, 5) * 1.2;
        }
    }
    fragColor = vec4(diffuseColor.rgb  * (1.0 + intensity), 1.0);
}