
// Incoming vertex position
in vec3 position;

// Incoming texture coordinates
in vec2 texCoord;

uniform mat4 model_matrix;

uniform mat4 view_matrix;

uniform mat4 proj_matrix;

// Interpolated tex coords for the fragment shader.
out vec2 texCoordFrag;

void main() {
	// The global position is in homogenous coordinates
    vec4 globalPosition = model_matrix * vec4(position, 1);

    // The position in camera coordinates
    vec4 viewPosition = view_matrix * globalPosition;

    // The position in CVV coordinates
    gl_Position = proj_matrix * viewPosition;

    texCoordFrag = texCoord;
}
