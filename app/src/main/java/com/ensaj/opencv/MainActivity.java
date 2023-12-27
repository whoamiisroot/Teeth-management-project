package com.ensaj.opencv;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity {
    private float[] lastTouchDownXY = new float[2];
    private Button select, camera,userProfileButton;
    private ImageView imageView;
    private Bitmap bitmap;
    private Mat mat, result;
    private MotionEvent lastTouchEvent;
    private int pointsClicked = 0, circleIndex = 0;
    private List<Integer> previousPoints = new ArrayList<>();
    private List<Point[]> pointsList = new ArrayList<>();
    private List<Point> selectedPoints = new ArrayList<>();
    private boolean pointsSelected = false;

    private static final int SELECT_CODE = 100;
    private static final int CAMERA_CODE = 101;
    // Declare these variables at the class level
    private PopupWindow anglePopup;
    private TextView textViewAngle1, textViewAngle2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userProfileButton = findViewById(R.id.user_profile);

        Intent userIntent = getIntent();
        if (userIntent != null) {
            String username = userIntent.getStringExtra("login"); // Assuming "login" is the key for the username
            userProfileButton.setText(username);
        }

        if (OpenCVLoader.initDebug()) {
            Log.e("OpenCv initialized", "Success!");
        } else {
            Log.d("OpenCv initialization", "Failed");
        }

        camera = findViewById(R.id.camera);
        select = findViewById(R.id.select);
        imageView = findViewById(R.id.imageView);
        userProfileButton = findViewById(R.id.user_profile);


        getPermission();
        Intent mainIntent = getIntent();
        if (mainIntent != null) {
            String userId = mainIntent.getStringExtra("userId");
            String firstName = mainIntent.getStringExtra("firstName");
            String lastName = mainIntent.getStringExtra("lastName");
            String role = mainIntent.getStringExtra("role");
            String login = mainIntent.getStringExtra("login");
            String password = mainIntent.getStringExtra("password");
            String number = mainIntent.getStringExtra("number");
            String group = mainIntent.getStringExtra("group");

            // Log user information
            Log.d("MainActivity", "Received user information - " +
                    "UserId: " + userId +
                    ", FirstName: " + firstName +
                    ", LastName: " + lastName +
                    ", Role: " + role +
                    ", Login: " + login +
                    ", Password: " + password +
                    ", Number: " + number +
                    ", Group: " + group);

        }


        select.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_CODE);
        });

        imageView.setOnTouchListener((v, event) -> {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                lastTouchDownXY[0] = event.getX();
                lastTouchDownXY[1] = event.getY();
            }
            return false;
        });
        userProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click, navigate to the user profile activity
                Intent mainIntent = getIntent();
                if (mainIntent != null) {
                    String userId = mainIntent.getStringExtra("userId");
                    String firstName = mainIntent.getStringExtra("firstName");
                    String lastName = mainIntent.getStringExtra("lastName");
                    String role = mainIntent.getStringExtra("role");
                    String login = mainIntent.getStringExtra("login");
                    String password = mainIntent.getStringExtra("password");
                    String number = mainIntent.getStringExtra("number");
                    String group = mainIntent.getStringExtra("group");

                    Log.d("MainActivity", "Received user information - " +
                            "UserId: " + userId +
                            ", FirstName: " + firstName +
                            ", LastName: " + lastName +
                            ", Role: " + role +
                            ", Login: " + login +
                            ", Password: " + password +
                            ", Number: " + number +
                            ", Group: " + group);

                    // Pass user information to UserProfile
                    Intent userProfileIntent = new Intent(MainActivity.this, UserProfile.class);
                    userProfileIntent.putExtra("userId", userId);
                    userProfileIntent.putExtra("firstName", firstName);
                    userProfileIntent.putExtra("lastName", lastName);
                    userProfileIntent.putExtra("role", role);
                    userProfileIntent.putExtra("login", login);
                    userProfileIntent.putExtra("password", password);
                    userProfileIntent.putExtra("number", number);
                    userProfileIntent.putExtra("group", group);
                    Log.e("pasword dyal maiiin", password);

                    // Start UserProfile activity
                    startActivity(userProfileIntent);
                }
            }
        });
        imageView.setOnClickListener(v -> handleImageViewClick());

        camera.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_CODE);
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.lastTouchEvent = event;
        handleImageViewClick();
        return true;
    }

    private void handleImageViewClick() {
        if (pointsSelected) {
            return;
        }
        float x = lastTouchDownXY[0];
        float y = lastTouchDownXY[1];

        double minDistance = Double.MAX_VALUE;
        int closestIndex = -1;

        Point[] points = new Point[0];
        for (int i = 0; i < pointsList.size(); i++) {
            points = pointsList.get(i);
            for (int j = 0; j < points.length; j++) {
                Point circleCenter = points[j];
                double distance = Math.sqrt(Math.pow(x - circleCenter.x, 2) + Math.pow(y - circleCenter.y, 2));
                if (distance < minDistance) {
                    minDistance = distance;
                    closestIndex = j;
                }
            }
        }

        if (pointsClicked < 4 && closestIndex != -1 && !previousPoints.contains(closestIndex)) {
            Imgproc.circle(result, points[closestIndex], 7, new Scalar(255, 0, 0), -1);
            selectedPoints.add(points[closestIndex]);
            previousPoints.add(closestIndex);
            pointsClicked += 1;
        }

        if (pointsClicked == 2) {
            // Draw a line between the 1st and 2nd points
            Point p1 = selectedPoints.get(0);
            Point p2 = selectedPoints.get(1);
            Imgproc.line(result, p1, p2, new Scalar(0, 255, 0), 2);

            // Calculate the angle between the first line and the vertical line
            double angle1 = calculateAngle(p1, p2);
            // Display the angle value beside the first line
            putText(result, String.format("%.2f", angle1), p1, p2);
        } else if (pointsClicked == 4) {
            // Calculate the angles between the lines
            double angle1 = calculateAngle(selectedPoints.get(0), selectedPoints.get(1));
            double angle2 = calculateAngle(selectedPoints.get(2), selectedPoints.get(3));

            // Call the method to show the pop-up with both calculated angles
            showAnglePopup(angle1, angle2);

            // Clear the selected points after showing the pop-up
            selectedPoints.clear();
            pointsClicked = 0;
            previousPoints.clear();

            // Clear the drawing on the result Mat
            result = new Mat(mat.size(), CvType.CV_8UC3, new Scalar(0, 0, 0));
            Imgproc.cvtColor(mat, result, Imgproc.COLOR_GRAY2RGB);
            Utils.matToBitmap(result, bitmap);
            imageView.setImageBitmap(bitmap);
            processEdges();
            pointsSelected = false;
        }

        Log.e("les points", selectedPoints.toString());

        if (closestIndex != -1) {
            Log.d("Closest Point", "Index: " + closestIndex);
            Utils.matToBitmap(result, bitmap);
            imageView.setImageBitmap(bitmap);
        }
    }
    private void putText(Mat image, String text, Point p1, Point p2) {
        // Calculate the position to place the text between p1 and p2
        Point textPosition = new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);

        // Draw the text on the image
        Imgproc.putText(image, text, textPosition, Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(255, 255, 255), 2);
    }

    private double calculateAngle(Point p1, Point p2) {
        // Calculate the angle between the line and the horizontal axis
        double deltaY = p2.y - p1.y;
        double deltaX = p2.x - p1.x;
        double angle = Math.toDegrees(Math.atan2(deltaY, deltaX));

        // Ensure the angle is in the range [0, 360)
        angle = (angle + 360) % 360;
        Log.e("angle",angle+"");

        return angle;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_CODE && data != null) {
            processImageSelection(data);

        } else if (requestCode == CAMERA_CODE && data != null) {
            processCameraCapture(data);
        }
    }

    private void processImageSelection(Intent data) {
        previousPoints.clear();
        pointsClicked = 0;
        pointsSelected = false;

        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
            imageView.setImageBitmap(bitmap);

            mat = new Mat();
            Utils.bitmapToMat(bitmap, mat);
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);
            Imgproc.GaussianBlur(mat, mat, new Size(5, 5), 0);

            processEdges();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processCameraCapture(Intent data) {
        previousPoints.clear();
        pointsClicked = 0;
        bitmap = (Bitmap) data.getExtras().get("data");
        imageView.setImageBitmap(bitmap);

        mat = new Mat();
        Utils.bitmapToMat(bitmap, mat);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);
        Imgproc.GaussianBlur(mat, mat, new Size(7, 7), 0);

        processEdges();
    }

    private void processEdges() {
        Mat edges = new Mat();
        Imgproc.Canny(mat, edges, 50, 150);
        Imgproc.dilate(edges, edges, new Mat(), new Point(-1, -1), 2);

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(edges, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        result = new Mat(edges.rows(), edges.cols(), CvType.CV_8UC3, new Scalar(0, 0, 0));
        Imgproc.cvtColor(edges, result, Imgproc.COLOR_GRAY2RGB);

        pointsList.clear();

        for (MatOfPoint contour : contours) {
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            double epsilon = 0.0005 * Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true);
            Imgproc.approxPolyDP(new MatOfPoint2f(contour.toArray()), approxCurve, epsilon, true);

            Point[] points = approxCurve.toArray();
            pointsList.add(points);

            // Add corner detection logic here
            for (int i = 0; i < points.length; i++) {
                Point p1 = points[i];
                Point p2 = points[(i + 1) % points.length];
                Imgproc.circle(result, p1, 3, new Scalar(255, 255, 255), -1);
                // Include your corner detection logic here
                // For example, you can add the detected corners to a list or perform additional processing
                // based on the corner coordinates.
            }
        }

        // Display the result
        Utils.matToBitmap(result, bitmap);
        imageView.setImageBitmap(bitmap);
    }

    private void getPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 102);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 102 && grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            getPermission();
        }
    }


    // Inside your MainActivity class
// Inside your MainActivity class
    private void showAnglePopup(double angle1, double angle2) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_layout, null);

        textViewAngle1 = popupView.findViewById(R.id.textViewAngle1);
        textViewAngle2 = popupView.findViewById(R.id.textViewAngle2);

        // Set the calculated angles to the TextViews
        textViewAngle1.setText("Lingual 15°/à l'horizontal : " + String.format("%.2f", angle1));
        textViewAngle2.setText("Vestibulaire à 45 °/à l'horizontal : " + String.format("%.2f", angle2));

        Button closeButton = popupView.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> {
            if (anglePopup != null && anglePopup.isShowing()) {
                anglePopup.dismiss();
            }
        });

        // Create the PopupWindow
        anglePopup = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        anglePopup.setTouchable(true);
        anglePopup.setFocusable(true);

        // Show the pop-up at the center of the screen
        anglePopup.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }


}
