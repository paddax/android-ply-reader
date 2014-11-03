#version 150 core

in vec3 in_Position;
in vec3 in_Normal;

uniform int u_RenderMode;
uniform mat4 u_MMatrix;		// model matrix.      		       
uniform mat4 u_VMatrix;		// view matrix.       		
uniform mat4 u_PMatrix;     // projection matrix.
uniform vec4 u_Color;       // Fragment base color

out vec4 v_Color;
out vec3 v_Position;
out vec3 v_Normal;

void main(void) {
	mat4 modelview = u_VMatrix * u_MMatrix;
	mat3 normalMatrix = transpose(inverse(mat3(modelview)));
	mat4 mvpmatrix = u_PMatrix * modelview;

	gl_Position = mvpmatrix* vec4(in_Position,1); 
    v_Position = vec3(gl_Position);
   	v_Normal = normalize(normalMatrix * in_Normal);
   	//v_Color = u_Color;
   	// Injected transparency on color
	v_Color = vec4(u_Color.x, u_Color.y, u_Color.z, 0.4);
}
