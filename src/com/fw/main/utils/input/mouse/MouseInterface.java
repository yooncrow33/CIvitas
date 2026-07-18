package com.fw.main.utils.input.mouse;

public interface MouseInterface {
    void mouseClicked(FwMouseAPI e);
    void mousePressed(FwMouseAPI e);
    void mouseReleased(FwMouseAPI e);
    void mouseEntered(FwMouseAPI e);
    void mouseExited(FwMouseAPI e);
    void mouseWheelMoved(FwMouseAPI e);
}