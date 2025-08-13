package org.avsytem;
import java.util.Scanner;

import org.mindrot.jbcrypt.BCrypt;

public class Main {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.print("Digite sua senha: ");
        String senha = s.nextLine();
        String salt = BCrypt.gensalt();
        System.out.print(BCrypt.hashpw(senha, salt)); 
    }
}
