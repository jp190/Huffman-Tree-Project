
//completed by Yan Jiang & Jungmin Park

import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.PriorityQueue;
import java.util.Scanner;

public class HuffmanSubmit implements Huffman {

	class Node implements Comparable { // Node class
		char c;
		int freq;
		Node left;
		Node right;

		public Node(char c, int freq) {
			this.c = c;
			this.freq = freq;
		}

		@Override
		public int compareTo(Object o) {
			Node n = (Node) o;

			return freq - n.freq;
		}
	}

	HashMap<Character, Integer> map = new HashMap<>(); // stores the characters and their frequency

	public void check(HashMap<Character, Integer> map, char n) { // count the frequencies
		if (map.containsKey(n)) {
			int f = map.get(n);
			f = f + 1;
			map.put(n, f);
		} else {
			map.put(n, 1);
		}
	}

	public void freqFile(String inputFile, String freqFile) {

		BinaryIn in = new BinaryIn(inputFile); // read in file

		while (!in.isEmpty()) {
			char next = in.readChar();
			check(map, next);
		}

		BinaryOut out = new BinaryOut(freqFile); // output file
		for (char key : map.keySet()) {
			String s = format(Integer.toBinaryString(key)) + ":";// convert characters to binary strings
			int frequency = map.get(key);
			s += frequency + "\n";
			out.write(s);
		}

		out.flush();
		out.close();

	}

	public String format(String key) { // format all binary strings to 8 digits
		while (key.length() < 8) {
			key = "0" + key;
		}
		return key;
	}

	// traverse algorithm
	public static void traverseE(Node root, String s, HashMap<Character, String> dictionary) { // traverse for encode
																								// method to find
																								// huffman code for each
																								// character
		if (root.c != '-' || (root.left == null && root.right == null)) {
			dictionary.put(root.c, s);
		}
		if (root.left != null) {
			traverseE(root.left, s + "0", dictionary);
		}

		if (root.right != null) {
			traverseE(root.right, s + "1", dictionary);
		}
	}

	public static void traverseD(Node root, String s, HashMap<String, Character> dictionary) {// traverse for decode
																								// method
		if (root.c != '-' || (root.left == null && root.right == null)) {
			dictionary.put(s, root.c);
		}
		if (root.left != null) {
			traverseD(root.left, s + "0", dictionary);
		}

		if (root.right != null) {
			traverseD(root.right, s + "1", dictionary);
		}
	}
	// keep on adding the minimum two numbers in the frequency list and create a

	public void encode(String inputFile, String outputFile, String freqFile) {

		freqFile(inputFile, freqFile); // take in frequencies
		PriorityQueue<Node> treeQueue = new PriorityQueue<>();
		Scanner scan = null;

		try {
			scan = new Scanner(new File(freqFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while (scan.hasNext()) {
			String line = scan.nextLine();
			String[] pair = line.split(":");
			char nodeKey = (char) Integer.parseInt(pair[0], 2);
			int nodeValue = Integer.parseInt(pair[1]);
			// create new node
			Node newNode = new Node(nodeKey, nodeValue);
			// add node to treeQueue
			newNode.left = null;
			newNode.right = null;
			treeQueue.add(newNode);

		}

		while (treeQueue.size() > 1) { // making nodes for the huffman tree

			Node a1 = treeQueue.peek();
			treeQueue.poll();
			Node a2 = treeQueue.peek();
			treeQueue.poll();
			Node newNode1 = new Node('-', a1.freq + a2.freq);

			newNode1.left = a1;
			newNode1.right = a2;
			treeQueue.add(newNode1);
		}

		// final huffman tree
		Node tree = treeQueue.poll(); // huffman tree
		HashMap<Character, String> dictionary = new HashMap<>();// This Hashmap contains the pair of code of alphabet
																// and the huffman code
		traverseE(tree, "", dictionary);// find huffman code

		BinaryIn input = new BinaryIn(inputFile);
		BinaryOut bout = new BinaryOut(outputFile);

		while (!input.isEmpty()) {
			char tempC = input.readChar();
			String temp = dictionary.get(tempC);// huffman code in dictionary
			bout.write(temp);
		}

		bout.close();

	}

	// decode
	public void decode(String inputFile, String outputFile, String freqFile) {

		PriorityQueue<Node> treeQueue = new PriorityQueue<>();
		Scanner scan = null;

		try {
			scan = new Scanner(new File(freqFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while (scan.hasNext()) {
			String line = scan.nextLine();
			String[] pair = line.split(":");
			char nodeKey = (char) Integer.parseInt(pair[0], 2);
			int nodeValue = Integer.parseInt(pair[1]);
			Node newNode = new Node(nodeKey, nodeValue);
			newNode.left = null;
			newNode.right = null;
			treeQueue.add(newNode);

		}

		while (treeQueue.size() > 1) { // making nodes for the tree

			Node a1 = treeQueue.poll();
			Node a2 = treeQueue.poll();
			Node newNode1 = new Node('-', a1.freq + a2.freq);
			newNode1.left = a1;
			newNode1.right = a2;
			treeQueue.add(newNode1);
		}

		Node tree = treeQueue.poll();// huffman tree
		HashMap<String, Character> dictionary = new HashMap<>();// Hashmap contains the pair of code of alphabet and the
																// Huffman code
		traverseD(tree, "", dictionary);// find huffman code

		BinaryIn input = new BinaryIn(inputFile);
		BinaryOut bout = new BinaryOut(outputFile);

		String tempC = input.readChar() + "";
		while (!input.isEmpty()) { // replace huffman code in input file with matched binary string
			if (dictionary.containsKey(tempC)) {
				char tempA = dictionary.get(tempC);
				bout.write(tempA);
				tempC = "";
				if (!input.isEmpty()) {
					tempC = input.readChar() + "";
				}
			} else {
				tempC += input.readChar();
			}
		}
		bout.flush();
		bout.close();

	}

	public static void main(String[] args) throws FileNotFoundException {
		HuffmanSubmit huffman = new HuffmanSubmit();
		huffman.encode("ur.jpg", "ur.enc", "freq.txt");
		huffman.decode("ur.enc", "ur_dec.jpg", "freq.txt");
		huffman.freqFile("alice30.txt", "freq.txt");
	}

}
