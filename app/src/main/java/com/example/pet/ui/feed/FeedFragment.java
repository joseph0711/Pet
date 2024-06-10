package com.example.pet.ui.feed;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.pet.ConnectionMysqlClass;
import com.example.pet.MainActivity;
import com.example.pet.R;
import com.example.pet.SharedViewModel;
import com.example.pet.UserClass;
import com.example.pet.databinding.FragmentFeedBinding;
import com.example.pet.ui.feedAutomatic.FeedAutomaticFragment;
import com.example.pet.ui.feedManual.FeedManualFragment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FeedFragment extends Fragment {
    private FragmentFeedBinding binding;
    ConnectionMysqlClass connectionMysqlClass;
    Connection con;
    String str;
    private ImageView petImageView;
    public static Dialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFeedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        petImageView = root.findViewById(R.id.feed_imgAvatarPet);

        connectionMysqlClass = new ConnectionMysqlClass();
        connect(this::findPetAvatarById);

        // Initialize the Progress Bar Dialog
        progressDialog = new Dialog(requireActivity());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.dialog_progress);
        progressDialog.setCancelable(false);

        Button btnManual = root.findViewById(R.id.feed_btnManual);
        Button btnAutomatic = root.findViewById(R.id.feed_btnAutomatic);

        // Navigate to FeedManualFragment when the Manual Feed button is clicked
        btnManual.setOnClickListener(view -> {
            // Show the progress bar
            progressDialog.show();
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.feedManualFragment);
        });

        // Navigate to FeedAutomaticFragment when the Automatic Feed button is clicked
        btnAutomatic.setOnClickListener(view -> {
            // Show the progress bar
            progressDialog.show();
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.feedAutomaticFragment);
        });
        return root;
    }

    private void findPetAvatarById() {
        // Get the SharedViewModel instance
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        Bitmap petAvatar = sharedViewModel.getPetAvatar().getValue();
        if (petAvatar != null) {
            // Pet avatar is already available in the ViewModel, use it directly
            petImageView.setImageBitmap(petAvatar);
        } else {
            // Pet avatar is not available in the ViewModel, read it from the database
            String sql = "SELECT pet.PetImage as PetImage FROM user JOIN pet ON user.id = pet.id WHERE user.id = ?;";

            // Get the UserClass object from the SharedViewModel
            UserClass userClass = sharedViewModel.getUserClass().getValue();

            // Get the id from the UserClass object
            int id = userClass.getId();

            ExecutorService executionService = Executors.newSingleThreadExecutor();
            executionService.execute(() -> {
                try {
                    PreparedStatement preparedStatement = con.prepareStatement(sql);
                    preparedStatement.setInt(1, id);

                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        // Retrieve the image data.
                        byte[] petImageBytes = resultSet.getBytes("PetImage");

                        // Convert the byte arrays to Bitmaps.
                        Bitmap petBitmap = BitmapFactory.decodeByteArray(petImageBytes, 0, petImageBytes.length);

                        // Set the Bitmaps to ImageViews.
                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> {
                                if (petImageView != null) {
                                    petImageView.setImageBitmap(petBitmap);
                                }
                            });

                            // Store the pet avatar in the ViewModel for future use
                            sharedViewModel.getPetAvatar().postValue(petBitmap);
                        }
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
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
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).showBottomNavigationView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}