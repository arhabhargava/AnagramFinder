/**
 * Read name and dictionary file
 * and display all anagrams in name.
 * Build a phrase from the available anagrams.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class AnagramPhrase {
    static ArrayList<String> dict, anagrams, phrase;
    static TreeMap<Character, Integer> nameLetterMap;
    static String inputName, remaining;

    /**
     * @param args  the dictionary words in a text file
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        // Load a dictionary file
        if (args.length != 1) {
            System.out.println("Usage: java AnagramPhrase dictionaryFileName");
            System.exit(1);
        }
        dict = buildDictionary(args[0]);
//        phrase = new ArrayList<String>(); //TODO: put this in the method where it belongs ------DONE-----

        // Accept a name from user
        System.out.print("Enter a phrase: ");
        Scanner scan = new Scanner(System.in);
        setPhrase(scan.nextLine());

        // Set limit = length of name
        int limit = getPhraseLen(inputName);

        // Start empty list to hold anagram phrase
        phrase = new ArrayList<>();

        while (phraseLength() < limit) {

            // Generate list of dictionary words that fit in name
            anagrams = findAnagrams(remaining);

            // Present words to user
            //System.out.println("ANAGRAMS:");
            displayWords(anagrams);

            // Present current phrase to user
            System.out.println("Current phrase: " + getCurrentPhrase());

            // Present remaining letters to user
            System.out.println("Remaining letters: " + getRemainingLetters(remaining));
            System.out.println("Number of remaining letters: " + remaining.length());

            // Ask user to input word or start over
            String nextWord = getUserInput(scan);
            if (nextWord.equalsIgnoreCase("S")) {
                reset();
                continue;
            }
            // If user input can be made from remaining letters
            if (hasMoreAnagrams(nextWord)) {

                // Accept choice of new word or words from user
                String response = getUserInput(scan, nextWord);
                if (response.equalsIgnoreCase("S")) {
                    reset();
                }

                phrase.add(nextWord);

                // Remove letters in choice from letters in name
                remaining = removeWord(nextWord);
                getRemainingLetters(remaining);

                // Return choice and remaining letters in name
            } else {
                if (lastWord(nextWord) && isAnagram(nextWord)) {
                    phrase.add(nextWord);
                    remaining = removeWord(nextWord);
                } else {
                    // If choice is not a valid selection:
                    // Ask user for new choice or let user start over
                    String response = getUserInput(scan, true);
                    if (response.equalsIgnoreCase("Q"))
                        break;
                    else
                        reset();
                }
            }
        }
        // When phrase length equals limit value:
        // Display final phrase
        System.out.print("Final phrase: " + getCurrentPhrase());
    }

    /**
     * If this word is an anagram contained within the remaining
     * letters of the original phrase and if, when the letters of
     * this word are removed from it, there will still be anagrams
     * left to form more words in the new phrase, then return true.
     * @param word - the candidate word to add to the phrase
     * @return
     */
    public static boolean hasMoreAnagrams(String word) {
        if (!isAnagram(word))
            return false;
        String tmp = removeWord(word);
        return (findAnagrams(tmp).size() > 0);
    }

    /**
     * Verify that this word is contained in the list of anagrams
     * contained in the remaining original phrase
     * @param word
     * @return
     */
    public static boolean isAnagram(String word) {
        return findAnagrams(remaining).contains(word);
    }

    /**
     * @param word
     * @return true if this is the final word that can be made
     *         from the remaining letters in the original phrase
     */
    public static boolean lastWord(String word) {
        return remaining.length() - word.length() == 0;
    }

    /**
     * Remove the characters contained in the string parameter from the currently
     * remaining in the original phrase/string
     * @param word
     * @return the string remaining from the original phrase
     */
    public static String removeWord(String word) {

        String tmpRemaining = new String(remaining);

        // Removes the word
        tmpRemaining = tmpRemaining.replaceAll(word, "");

        return tmpRemaining;
    }

    /**
     * @param input Scanner object
     * @return user choice
     */
    public static String getUserInput(Scanner input) {
        System.out.print("\tEnter 'S' to start over or a word from the list: ");
        return input.nextLine();
    }

    /**
     * @param input Scanner object
     * @param newWord
     * @return user choice
     */
    public static String getUserInput(Scanner input, String newWord) {
        System.out.print("\tEnter 'S' to start over or Enter to accept: " + newWord);
        return input.nextLine();
    }

    /**
     * @param input Scanner object
     * @param hasQuitOption
     * @return user choice
     */
    public static String getUserInput(Scanner input, boolean hasQuitOption) {
        System.out.print("\tEnter 'Q' to quit else start over: ");
        return input.nextLine();
    }

    /**
     * @return String of words in ArrayList phrase separated by a space
     */
    public static String getCurrentPhrase() {
        phrase = new ArrayList<String>();

        String currPhrase = "";
        for (String s : phrase)
            currPhrase += s + " ";
        return currPhrase;
    }


    /**
     * Sort the letters remaining in the original phrase
     * @param name
     * @return sorted string of characters
     */
    public static String getRemainingLetters(String name) {
        char[] tmp = name.toCharArray();
        Arrays.sort(tmp);
        StringBuilder str = new StringBuilder();
        for (char ch : tmp)
            str.append(ch);
        return str.toString();
    }

    /**
     * Display four columns of strings in list
     * @param list
     */
    public static void displayWords(ArrayList<String> list) {
        int count = 1;
        for (String word : list) {
            System.out.printf("%15s", word);
            if (count++ % 4 == 0)
                System.out.println();
        }
        System.out.println();
    }


    /**
     * Set phrase variable from line removing spaces and upperCase
     * @param line
     */
    public static void setPhrase(String line) {
        // TODO: rename this to what it really does
        //No need to rename
        inputName = line.replace(" ", "").toLowerCase();
        reset();
    }

    /**
     * Reinitialize variable remaining with original input phrase
     * (with spaces and upperCase removed).
     */
    public static void reset() {
        remaining = new String(inputName);
        //Check for null phrase
        phrase = new ArrayList<String>();
    }

    /**
     * @param word
     * @return the length of the string (not including spaces)
     */
    public static int getPhraseLen(String word) {

        // This code is not needed
//        int length = word.length();
//        for (int i = 0; i < word.length(); ++i) {
//            if (Character.isWhitespace(word.charAt(i))) {
//                length--;
//            }
//        }
        return word.length();
    }

    /**
     * Construct a list of words from filename.  Use BufferedReader and readLine().
     * Close the BufferedReader object when done.
     *
     * @param filename of a file that has one word per line
     * @return ArrayList<String> the list
     * @throws IOException
     */
    public static ArrayList<String> buildDictionary(String filename) throws IOException {

        dict = new ArrayList<String>();
        BufferedReader buffReader = new BufferedReader(new FileReader(filename));

        String line = buffReader.readLine(); //Reads a line
        while (line != null){
            dict.add(line); //Adds the line to dict
            line = buffReader.readLine(); //Reads next line
        }
        buffReader.close();
        return dict;
    }

    /**
     * Make a TreeMap<Character,Integer> from the testWord.
     * Initialize a new list for anagrams.
     * for each word in the dictionary list of words (dict),
     *    make a map
     *    if the test word map is contained in the dict word
     *       add the testWord to the anagram list
     * @param testWord
     * @return list of anagrams
     */
    public static ArrayList<String> findAnagrams(String testWord) {
        anagrams = new ArrayList<String>();

        TreeMap<Character, Integer> map1 = null;
        TreeMap<Character, Integer> map2 = makeMap(testWord); //Map creation for testWord

        for (String word : dict){
            map1 = makeMap(word); //Map creation for word
            if (containsWord(map1, map2)){
                anagrams.add(word);
            }
        }
        return anagrams;
    }


    /**
     * for each Character in letterMap1.keySet()
     *   if the char is in letterMap2 and occurs no more frequently than
     *   it occurs in letterMap2, then it could be part of the anagram
     * @param letterMap1
     * @param letterMap2
     * @return true is letterMap1 is contained in letterMap2
     */
    public static boolean containsWord(TreeMap<Character, Integer> letterMap1,
                                       TreeMap<Character, Integer> letterMap2) {
        //Iterating through keys in LetterMap1
        for (Character entry : letterMap1.keySet()){
            //Checking if the key in is letterMap 2
            // and if the value of letterMap1 > letterMap2
            if (letterMap1.getOrDefault(entry,0) > letterMap2.getOrDefault(entry,0)){
                return false;
            }
        }
        return true;
    }


    /**
     * Example: Given "sweettimes" the map is: ['e':3,'i':1,'m':1,'s':2,'t':2,'w':1]
     * @param word
     * @return a sorted key,value pair map for word
     */
    public static TreeMap<Character, Integer> makeMap(String word) {
        TreeMap<Character, Integer> map = new TreeMap<>();

        //Creates an array of all the letters
        char[] ch = word.toCharArray();

        //For loop iterating through all the letters
        for (char letter : ch ) {
                map.merge(letter, 1, (curr, inc) -> curr + inc);
        }
		return map;
    }


    /**
     * @return number of characters in the internal phrase the user is constructing
     */
    public static int phraseLength() {
        int len = 0;
        for (String word : phrase)
            len += word.length();
        return len;
    }
}

