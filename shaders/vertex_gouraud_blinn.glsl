// A shader that performs Gouraud shading (using Blinn-Phong)
//
// Note: This shader assumes there is no non-uniform scale in either the view
// or the model transform.

// Incoming vertex position
in vec3 position;

// Incoming normal
in vec3 normal;

uniform mat4 model_matrix;

uniform mat4 view_matrix;

uniform mat4 proj_matrix;

// Light properties
uniform vec3 lightPos;
uniform float lightIntensity;
uniform float ambientIntensity;

// Material properties
uniform float ambientCoeff;
uniform float diffuseCoeff;
uniform float specularCoeff;
uniform float phongExp;

out float intensity;

void main() {
	// The global position is in homogenous coordinates
    vec4 globalPosition = model_matrix * vec4(position, 1);

    // The position in camera coordinates
    vec4 viewPosition = view_matrix * globalPosition;

    // The position in CVV coordinates
    gl_Position = proj_matrix * viewPosition;

    // Compute the normal in view coordinates
    vec3 m = normalize(view_matrix*model_matrix * vec4(normal, 0)).xyz;

    // Compute the s, v and r vectors
    vec3 s = normalize(view_matrix*vec4(lightPos,1) - viewPosition).xyz;
    vec3 v = normalize(-viewPosition.xyz);
    vec3 h = (m + s)/2;

    float ambient = ambientIntensity*ambientCoeff;
    float diffuse = max(lightIntensity*diffuseCoeff*dot(m,s), 0.0);
    float specular;

    // Only show specular reflections for the front face
    if (dot(m,s) > 0)
        specular = max(lightIntensity*specularCoeff*pow(dot(h,v),phongExp), 0.0);
    else
        specular = 0;

    intensity = ambient + diffuse + specular;
}
