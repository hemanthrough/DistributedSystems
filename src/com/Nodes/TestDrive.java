package com.Nodes;

public class TestDrive {
	public static void main(String[] args) {
		Node nodeOn= new Node(8000, 1, 8001);
		Node nodeTwo = new Node(8001, 2, 8002);
		Node nodeThree = new Node(8002, 3, 8003);
		Node nodeFour = new Node(8003, 4, 8000);
		nodeTwo.beginElection();
	}
}
