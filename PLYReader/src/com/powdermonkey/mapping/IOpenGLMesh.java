package com.powdermonkey.mapping;

/**
 * Basic contract for controlling a mesh in an opengl context
 * 
 * @author pdavis@winbrogroup.com
 * 
 */
public interface IOpenGLMesh {

    /**
     * Attaches this object to the current OpenGL context
     * 
     */
    public void attach();

    /**
     * Draws the object to the current OpenGL context, the caller must ensure
     * the context is the same as the attach context
     */
    public void draw();

    /**
     * Detaches this object from the current OpenGL context
     */
    public void detach();

}
