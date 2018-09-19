
out vec4 outputColor;

uniform vec4 input_color;

uniform mat4 view_matrix;

// Light properties
uniform vec3 lightPos;
uniform vec3 lightIntensity;
uniform vec3 ambientIntensity;

// Material properties
uniform vec3 ambientCoeff;
uniform vec3 diffuseCoeff;
uniform vec3 specularCoeff;
uniform float phongExp;

in vec4 viewPosition;
in vec3 m;

void main()
{
    vec3 m_unit = normalize(m);
    // Compute the s, v and r vectors
    vec3 s = normalize(view_matrix*vec4(lightPos,1) - viewPosition).xyz;
    vec3 v = normalize(-viewPosition.xyz);
    vec3 r = normalize(reflect(-s,m_unit));

    vec3 ambient = ambientIntensity*ambientCoeff;
    vec3 diffuse = max(lightIntensity*diffuseCoeff*dot(m_unit,s), 0.0);
    vec3 specular;

    // Only show specular reflections for the front face
    if (dot(m_unit,s) > 0)
        specular = max(lightIntensity*specularCoeff*pow(dot(r,v),phongExp), 0.0);
    else
        specular = vec3(0);

    vec3 intensity = ambient + diffuse + specular;

    outputColor = vec4(intensity,1)*input_color;
}
