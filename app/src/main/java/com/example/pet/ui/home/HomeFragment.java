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
    private ImageView userImageView, petImageView;
    private TextView userTextView, petNameTextView, petAgeTextView, petWeightTextView;
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
        petImageView = root.findViewById(R.id.home_imgAvatarPet);
        petNameTextView = root.findViewById(R.id.home_petName);
        petAgeTextView = root.findViewById(R.id.home_petAge);
        petWeightTextView = root.findViewById(R.id.home_petWeight);

        userTextView.setText("Hello, " + name);

        connectionMysqlClass = new ConnectionMysqlClass();
        Log.e("debug msg", "id" + id);
        connectMysql(this::findUserAvatarById);
        connectMysql(this::findPetInfoById);
        return root;
    }

    private void findUserAvatarById() {
        String sql = "SELECT user.UserImage as UserImage FROM user JOIN pet ON user.id = pet.id WHERE user.id = ?;";

        ExecutorService executionService = Executors.newSingleThreadExecutor();
        executionService.execute(() -> {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setInt(1, id);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    // Retrieve the image data.
                    byte[] userImageBytes = resultSet.getBytes("UserImage");

                    // Convert the byte arrays to Bitmaps.
                    Bitmap userBitmap = BitmapFactory.decodeByteArray(userImageBytes, 0, userImageBytes.length);

                    // Set the Bitmaps to ImageViews.
                    requireActivity().runOnUiThread(() -> {
                        if (isAdded() && userImageView != null) {
                            userImageView.setImageBitmap(userBitmap);
                        }
                    });
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void findPetInfoById() {
        String sql = "SELECT pet.PetImage as PetImage, pet.PetName as PetName, pet.Age as Age, pet.Weight as Weight FROM pet WHERE pet.id = ?;";

        ExecutorService executionService = Executors.newSingleThreadExecutor();
        executionService.execute(() -> {
            try {
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setInt(1, id);

                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    // Retrieve the image data and pet information.
                    byte[] petImageBytes = resultSet.getBytes("PetImage");
                    String petName = resultSet.getString("PetName");
                    int petAge = resultSet.getInt("Age");
                    float petWeight = resultSet.getFloat("Weight");

                    // Convert the byte arrays to Bitmaps.
                    Bitmap petBitmap = BitmapFactory.decodeByteArray(petImageBytes, 0, petImageBytes.length);

                    // Set the values to the respective views.
                    requireActivity().runOnUiThread(() -> {
                        if (isAdded()) {
                            if (petImageView != null) {
                                petImageView.setImageBitmap(petBitmap);
                            }
                            if (petNameTextView != null) {
                                petNameTextView.setText(petName);
                            }
                            if (petAgeTextView != null) {
                                petAgeTextView.setText("Age: " + petAge + " years");
                            }
                            if (petWeightTextView != null) {
                                petWeightTextView.setText("Weight: " + petWeight + " kg");
                            }
                        }
                    });
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void connectMysql(Runnable onConnected) {
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
                    if (isAdded()) {
                        requireActivity().runOnUiThread(onConnected);
                    }
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