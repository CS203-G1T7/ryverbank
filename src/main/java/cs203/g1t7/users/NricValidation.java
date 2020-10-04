package cs203.g1t7.users;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/*  
    Source:
    Terence:squeeish (2019), Validation for Singapore NRIC and FIN number 
    https://gist.github.com/squeeish/65cc82b0acaea3f551eac6e7885dc9c5 
*/

public class NricValidation {
    public NricValidation() {}
    public boolean validateNric(String inputString) {
        String nricToTest = inputString.toUpperCase();

        // first letter must start with S, T, F or G. Last letter must be A - Z
        if (!Pattern.compile("^[STFG]\\d{7}[A-Z]$").matcher(nricToTest).matches()) {
            return false;
        } else {
            char[] icArray = new char[9];
            char[] st = "JZIHGFEDCBA".toCharArray();
            char[] fg = "XWUTRQPNMLK".toCharArray();

            for (int i = 0; i < 9; i++) {
                icArray[i] = nricToTest.charAt(i);
            }

            // calculate weight of positions 1 to 7
            int weight = (Integer.parseInt(String.valueOf(icArray[1]), 10)) * 2 + 
                    (Integer.parseInt(String.valueOf(icArray[2]), 10)) * 7 +
                    (Integer.parseInt(String.valueOf(icArray[3]), 10)) * 6 +
                    (Integer.parseInt(String.valueOf(icArray[4]), 10)) * 5 +
                    (Integer.parseInt(String.valueOf(icArray[5]), 10)) * 4 +
                    (Integer.parseInt(String.valueOf(icArray[6]), 10)) * 3 +
                    (Integer.parseInt(String.valueOf(icArray[7]), 10)) * 2;

            int offset = icArray[0] == 'T' || icArray[0] == 'G' ? 4 : 0;

            int lastCharPosition = (offset + weight) % 11;

            if (icArray[0] == 'S' || icArray[0] == 'T') {
                return icArray[8] == st[lastCharPosition];
            } else if (icArray[0] == 'F' || icArray[0] == 'G') {
                return icArray[8] == fg[lastCharPosition];
            } else {
                return false; // this line should never reached due to regex above
            }
        }
    }
}