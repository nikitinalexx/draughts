package com.alex.nikitin.server;

import com.alex.nikitin.server.model.Game;

public class Server {

    public static Game createNewGame() {
        return new Game();
    }
}
