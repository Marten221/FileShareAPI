package com.example.FileShareAPI.Back_End.constant;

import io.github.cdimascio.dotenv.Dotenv;

public class Constant {
    public static final String FILE_DIRECTORY = Dotenv.load().get("FILE_DIRECTORY");
}
