package com.example.pet.ui.home;

import static android.content.Context.MODE_PRIVATE;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.pet.ConnectionMysqlClass;
import com.example.pet.R;
import com.example.pet.SharedViewModel;
import com.example.pet.UserClass;
import com.example.pet.databinding.FragmentHomeBinding;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    ConnectionMysqlClass connectionMysqlClass;
    private ImageView userImageView;
    private TextView userTextView;
    int id;
    Connection con;
    String str, name;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Retrieve user's name and id from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        name = sharedPreferences.getString("name", "");
        id = sharedPreferences.getInt("id", 0);

        // Create a new UserClass object and set the id and name
        UserClass userClass = new UserClass();
        userClass.setId(id);
        userClass.setName(name);

        // Get the SharedViewModel instance
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Set the UserClass object in the SharedViewModel
        sharedViewModel.setUserClass(userClass);

        userImageView = root.findViewById(R.id.home_imgAvatarUser);

        userTextView = root.findViewById(R.id.home_textTitle);
        userTextView.setText("Hello, " + name);

        connectionMysqlClass = new ConnectionMysqlClass();
        connect(this::findUserById);
        return root;
    }

    private void findUserById() {
        String sql = "SELECT user.Image as UserImage FROM user JOIN pet ON user.id = pet.id WHERE user.id = ?;";

        ExecutorService executionService = Executors.newSingleThreadExecutor();
        executionService.execute(() -> {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setInt(1, id);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    // Retrieve the image data.
                    byte[] userImageBytes = resultSet.getBytes("UserImage");
                    //byte[] petImageBytes = resultSet.getBytes("PetImage");

                    // Convert the byte arrays to Bitmaps.
                    Bitmap userBitmap = BitmapFactory.decodeByteArray(userImageBytes, 0, userImageBytes.length);
                    //Bitmap petBitmap = BitmapFactory.decodeByteArray(petImageBytes, 0, petImageBytes.length);

                    // Set the Bitmaps to ImageViews.
                    requireActivity().runOnUiThread(() -> {
                        if (userImageView != null) {
                            userImageView.setImageBitmap(userBitmap);
                            //petImageView.setImageBitmap(petBitmap);
                        }
                    });
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void connect(Runnable onConnected) {
        ExecutorService executionService = Executors.newSingleThreadExecutor();
        executionService.execute(() -> {
            try {
                con = connectionMysqlClass.CONN();
                if (con == null) {
                    requireActivity().runOnUiThread(() -> {
                        str = "Error in connection with SQL server";
                        Toast.makeText(requireActivity(), str, Toast.LENGTH_SHORT).show();
                    });
                } else {
                    requireActivity().runOnUiThread(onConnected);
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}