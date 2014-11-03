#version 150 core

in vec4 v_Color;
in vec3 v_Position;
in vec3 v_Normal;

uniform int u_RenderMode;

out vec4 out_Color;

const float shininess = 10;
const float specular = 0.4;
const float ambient = 0.2;

const vec3 c_LightPos1 = vec3(0,0,-30);
const vec3 c_LightPos2 = vec3(-10,0,-30);

float specularLight(vec3 ldir) {
	vec3 h = normalize(ldir + v_Position);  
   	// compute the specular term into spec
   	float intSpec = max(dot(h,v_Normal), 0.0);
   	return specular * pow(intSpec,shininess);		
}


void main(void) {

	if(u_RenderMode == 0) {
		// Will be used for attenuation.
	    //float dist = distance(c_LightPos1, v_Position);                  
	
		// Get a lighting direction vector from the light to the vertex.
    	vec3 lv1 = normalize(c_LightPos1);              	
    	vec3 lv2 = normalize(c_LightPos2 - v_Normal);              	

		float intensity = max(dot(v_Normal,lv1), 0.0);
		//intensity += max(dot(v_Normal,lv2), 0.0);
		if (intensity > 0) {
			intensity = min(1, intensity);
        	out_Color = max(intensity *  v_Color + specularLight(lv1) /* + specularLight(lv2) */, ambient);
		}
		else {
		float intensity = max(dot(-v_Normal,lv1), 0.0);
		intensity += max(dot(-v_Normal,lv2), 0.0);
		    out_Color = v_Color * intensity / 3;
		}
	}
	else {
		out_Color = v_Color;
	}
}

