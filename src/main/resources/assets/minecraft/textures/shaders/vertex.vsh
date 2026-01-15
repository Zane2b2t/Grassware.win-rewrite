#version 120

void main() {
    gl_TexCoord[0] = gl_MultiTexCoord0;
    gl_FrontColor = gl_Color;   // <<<<<< THIS IS THE MISSING LINE
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}
