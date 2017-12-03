package chebotarskyi.dm;

import org.apache.lucene.queryparser.classic.ParseException;

import javax.swing.*;

public class MainGUI {

    private JPanel rootPanel;
    private JTextField link_secsion_link_field;
    private JTextField link_secsion_count_field;
    private JLabel link_secsion_error_label;
    private JButton processButton;
    private JTextPane result_pane;
    private JButton doItButton;
    private JTextField queryToProcessTextField;
    private JTextField urlPartQueryField;
    private JCheckBox exactMatchCheckBox;

    private QueryProcessor queryProcessor;

    public static void main(String[] args) {

        MainGUI mainGUI = new MainGUI();

        JFrame frame = new JFrame("MainGUI");
        frame.setContentPane(new MainGUI().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 500);

        frame.setVisible(true);
    }

    private MainGUI() {

        queryProcessor = new QueryProcessor();
        processButton.addActionListener(e -> processButtonClick());
        doItButton.addActionListener(e -> queryButtonClick());
    }


    private void processButtonClick() {

        queryProcessor.closeIndex();

        String url = link_secsion_link_field.getText();
        int count;
        try {
            count = Integer.parseInt(link_secsion_count_field.getText());
        } catch (NumberFormatException e) {
            link_secsion_error_label.setText("Incorrect number");
            return;
        }
        if (!Validator.isLinkValid(url)) {
            link_secsion_error_label.setText("Incorrect link");
            return;
        }

        IndexUtils indexUtils = new IndexUtils();

        LinkProcessor linkProcessor = new LinkProcessor(count, indexUtils);
        linkProcessor.startProcessing(url);

        indexUtils.closeWriter();

        link_secsion_error_label.setText("Done");
    }

    private void queryButtonClick() {
        queryProcessor = new QueryProcessor();

        StringBuilder builder = new StringBuilder();

        if (!urlPartQueryField.getText().isEmpty()) {
            builder.append("url: ");
            builder.append(urlPartQueryField.getText());
            builder.append("*");
        }

        if (!queryToProcessTextField.getText().isEmpty()) {
            if (!builder.toString().isEmpty())
                builder.append(" AND ");
            builder.append("body:");
            if (exactMatchCheckBox.isSelected()) {
                builder.append("\"");
                builder.append(queryToProcessTextField.getText());
                builder.append("\"");
            } else {
                builder.append(queryToProcessTextField.getText());
            }
        }

        System.out.println(builder.toString());

        try {
            result_pane.setText(queryProcessor.processQuery(builder.toString()));
        } catch (ParseException e) {
            result_pane.setText("Error while parsing query");
        }
    }

}
