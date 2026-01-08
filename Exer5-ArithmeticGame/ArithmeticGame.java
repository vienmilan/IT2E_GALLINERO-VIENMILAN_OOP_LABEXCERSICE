import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

public class ArithmeticGame extends JFrame {
	private JLabel num1Label, num2Label, operatorLabel;
	private JLabel correctLabel, wrongLabel;
	private JLabel correctValueLabel, wrongValueLabel;
	private JTextField answerField;
	private JButton submitButton;
	private JRadioButton[] operationButtons;
	private JRadioButton[] levelButtons;
	private ButtonGroup operationGroup, levelGroup;
	private int num1, num2, correctAnswer, correctCount = 0, wrongCount = 0;
	private Random rand = new Random();

	public ArithmeticGame() {
		setTitle("Arithmetic Game");
		setSize(640, 420);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout(10, 10));

		// ----- Title -----
		JLabel titleLabel = new JLabel("Arithmetic Game", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
		add(titleLabel, BorderLayout.NORTH);

		// ----- Options (WEST) -----
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new GridLayout(0, 1, 6, 6));
		optionsPanel.setBorder(BorderFactory.createTitledBorder("Options"));

		// Operations group
		JLabel opsTitle = new JLabel("Operations:");
		optionsPanel.add(opsTitle);
		String[] ops = {"Addition", "Subtraction", "Multiplication", "Division", "Mixed"};
		operationButtons = new JRadioButton[ops.length];
		operationGroup = new ButtonGroup();
		for (int i = 0; i < ops.length; i++) {
			operationButtons[i] = new JRadioButton(ops[i]);
			operationGroup.add(operationButtons[i]);
			optionsPanel.add(operationButtons[i]);
		}
		operationButtons[0].setSelected(true);

		// Levels group
		JLabel levelTitle = new JLabel("Level:");
		optionsPanel.add(levelTitle);
		String[] levels = {"Level 1 (1–10)", "Level 2 (11–50)", "Level 3 (51–100)"};
		levelButtons = new JRadioButton[levels.length];
		levelGroup = new ButtonGroup();
		for (int i = 0; i < levels.length; i++) {
			levelButtons[i] = new JRadioButton(levels[i]);
			levelGroup.add(levelButtons[i]);
			optionsPanel.add(levelButtons[i]);
		}

		levelButtons[0].setSelected(true);

		add(optionsPanel, BorderLayout.WEST);

		// ----- Equation (CENTER) -----
		JPanel equationWrapper = new JPanel(new BorderLayout());
		JPanel equationRow = new JPanel(new GridLayout(1, 5, 10, 10));
		num1Label = new JLabel("0", SwingConstants.CENTER);
		num1Label.setFont(new Font("Arial", Font.BOLD, 36));
		num1Label.setPreferredSize(new Dimension(120, 90));
		num1Label.setBorder(new LineBorder(Color.GRAY, 2));
		operatorLabel = new JLabel("+", SwingConstants.CENTER);
		operatorLabel.setFont(new Font("Arial", Font.BOLD, 36));
		num2Label = new JLabel("0", SwingConstants.CENTER);
		num2Label.setFont(new Font("Arial", Font.BOLD, 36));
		num2Label.setPreferredSize(new Dimension(120, 90));
		num2Label.setBorder(new LineBorder(Color.GRAY, 2));
		JLabel equalsLabel = new JLabel("=", SwingConstants.CENTER);
		equalsLabel.setFont(new Font("Arial", Font.BOLD, 36));
		answerField = new JTextField();
		answerField.setHorizontalAlignment(SwingConstants.CENTER);
		answerField.setFont(new Font("Arial", Font.PLAIN, 28));
		answerField.setPreferredSize(new Dimension(120, 90));
		answerField.setBorder(new LineBorder(Color.GRAY, 2));

		// Restrict input to optional leading '-' and digits only
		((AbstractDocument) answerField.getDocument()).setDocumentFilter(new DocumentFilter() {
			@Override
			public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
				if (isNumericInput(fb.getDocument().getText(0, fb.getDocument().getLength()), string, offset)) {
					super.insertString(fb, offset, string, attr);
				}
			}

			@Override
			public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
				if (isNumericInput(fb.getDocument().getText(0, fb.getDocument().getLength()), text, offset)) {
					super.replace(fb, offset, length, text, attrs);
				}
			}

			private boolean isNumericInput(String existing, String input, int offset) {
				StringBuilder sb = new StringBuilder(existing);
				if (input != null) sb.insert(offset, input);
				String result = sb.toString();
				if (result.isEmpty()) return true; // allow empty for user edits/backspace
				// allow leading negative sign
				if (result.equals("-")) return true;
				for (int i = 0; i < result.length(); i++) {
					char c = result.charAt(i);
					if (i == 0 && c == '-') continue;
					if (!Character.isDigit(c)) return false;
				}
				return true;
			}
		});
		equationRow.add(num1Label);
		equationRow.add(operatorLabel);
		equationRow.add(num2Label);
		equationRow.add(equalsLabel);
		equationRow.add(answerField);
		equationWrapper.add(equationRow, BorderLayout.CENTER);

		JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
		submitButton = new JButton("Submit");
		actionRow.add(submitButton);
		equationWrapper.add(actionRow, BorderLayout.SOUTH);

		add(equationWrapper, BorderLayout.CENTER);

		// ----- Score (SOUTH) -----
		JPanel scoreBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 6));
		correctLabel = new JLabel("Correct:");
		wrongLabel = new JLabel("Incorrect:");
		correctValueLabel = new JLabel("0", SwingConstants.CENTER);
		wrongValueLabel = new JLabel("0", SwingConstants.CENTER);
		Dimension scoreBox = new Dimension(40, 28);
		correctValueLabel.setPreferredSize(scoreBox);
		wrongValueLabel.setPreferredSize(scoreBox);
		correctValueLabel.setBorder(new LineBorder(Color.GRAY, 2));
		wrongValueLabel.setBorder(new LineBorder(Color.GRAY, 2));
		scoreBar.add(correctLabel);
		scoreBar.add(correctValueLabel);
		scoreBar.add(wrongLabel);
		scoreBar.add(wrongValueLabel);
		add(scoreBar, BorderLayout.SOUTH);

		// Attach listeners for operations and levels now that UI labels exist.
		for (JRadioButton b : operationButtons) {
			b.addItemListener(e -> {
				if (e.getStateChange() == ItemEvent.SELECTED) generateQuestion();
			});
		}

		for (JRadioButton b : levelButtons) {
			b.addItemListener(e -> {
				if (e.getStateChange() == ItemEvent.SELECTED) generateQuestion();
			});
		}

		// ----- Actions -----
		submitButton.addActionListener(e -> checkAnswer());
		answerField.addActionListener(e -> checkAnswer());

		// Start with first question
		generateQuestion();
	}

	private void generateQuestion() {
		String operation = getSelectedOperation();
		int level = getSelectedLevelIndex();

		int min = 1, max = 10;
		if (level == 0) { min = 1; max = 10; }
		else if (level == 1) { min = 11; max = 50; }
		else { min = 51; max = 100; }

		// Determine operator
		String opSymbol;
		if ("Mixed".equals(operation)) {
			String[] ops = {"+", "-", "*", "/"};
			opSymbol = ops[rand.nextInt(4)];
		} else {
			switch (operation) {
				case "Addition" -> opSymbol = "+";
				case "Subtraction" -> opSymbol = "-";
				case "Multiplication" -> opSymbol = "*";
				case "Division" -> opSymbol = "/";
				default -> opSymbol = "+";
			}
		}
		operatorLabel.setText(opSymbol);

		// Generate operands based on operator to ensure clean division
		if ("/".equals(opSymbol)) {
			// Ensure non-zero divisor and integer result
			num2 = rand.nextInt(max - min + 1) + min; // min >= 1, so non-zero
			int quotient = rand.nextInt(max - min + 1) + min;
			num1 = quotient * num2;
		} else {
			num1 = rand.nextInt(max - min + 1) + min;
			num2 = rand.nextInt(max - min + 1) + min;
		}

		// Compute correct answer
		switch (opSymbol) {
			case "+" -> correctAnswer = num1 + num2;
			case "-" -> correctAnswer = num1 - num2;
			case "*" -> correctAnswer = num1 * num2;
			case "/" -> correctAnswer = num1 / num2;
		}

		num1Label.setText(String.valueOf(num1));
		num2Label.setText(String.valueOf(num2));
		answerField.setText("");
		answerField.requestFocusInWindow();
	}

	private String getSelectedOperation() {
		for (JRadioButton b : operationButtons) {
			if (b.isSelected()) return b.getText();
		}
		return "Addition";
	}

	private int getSelectedLevelIndex() {
		for (int i = 0; i < levelButtons.length; i++) {
			if (levelButtons[i].isSelected()) return i;
		}
		return 0;
	}

	private void checkAnswer() {
		try {
			int playerAnswer = Integer.parseInt(answerField.getText().trim());
			if (playerAnswer == correctAnswer) {
				JOptionPane.showMessageDialog(this, "Correct!", "Result", JOptionPane.INFORMATION_MESSAGE);
				correctCount++;
			} else {
				JOptionPane.showMessageDialog(this, "Incorrect! The correct answer is " + correctAnswer, "Result", JOptionPane.ERROR_MESSAGE);
				wrongCount++;
			}
			correctValueLabel.setText(String.valueOf(correctCount));
			wrongValueLabel.setText(String.valueOf(wrongCount));
			generateQuestion();
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Please enter a valid number!", "Error", JOptionPane.WARNING_MESSAGE);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new ArithmeticGame().setVisible(true);
		});
	}
}