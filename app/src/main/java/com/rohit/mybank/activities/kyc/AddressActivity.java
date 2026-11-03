package com.rohit.mybank.activities.kyc;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.rohit.mybank.R;
import com.rohit.mybank.model.customer.KycRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AddressActivity extends AppCompatActivity {

    // ============================================================
    // VIEWS
    // ============================================================

    private EditText etAddress;
    private AutoCompleteTextView etCity;
    private AutoCompleteTextView etState;
    private EditText etPincode;

    private Button btnNext;

    // ============================================================
    // DATA
    // ============================================================

    private KycRequest request;

    private boolean editMode = false;

    private static final String EXTRA_KYC = "kyc";
    private static final String EXTRA_EDIT_MODE = "edit_mode";

    private ArrayAdapter<String> stateAdapter;
    private ArrayAdapter<String> cityAdapter;

    private final Map<String, List<String>> citiesByState =
            createCityMap();

    private final List<String> states =
            new ArrayList<>(citiesByState.keySet());

    /*
     * These variables record the actual dropdown selections.
     *
     * Typed text alone is not considered a valid selection.
     */
    private String selectedState = null;
    private String selectedCity = null;

    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_address);

        editMode = getIntent().getBooleanExtra(
                EXTRA_EDIT_MODE,
                false
        );

        request = getKycFromIntent();

        if (request == null) {

            Toast.makeText(
                    this,
                    "KYC data not found",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        initializeViews();

        configureStateDropdown();

        configureCityDropdown();

        populateExistingData();

        btnNext.setOnClickListener(
                v -> validateAndContinue()
        );
    }

    // ============================================================
    // GET KYC FROM INTENT
    // ============================================================

    @SuppressWarnings("deprecation")
    private KycRequest getKycFromIntent() {

        Intent intent = getIntent();

        if (intent == null) {
            return null;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            return intent.getSerializableExtra(
                    EXTRA_KYC,
                    KycRequest.class
            );

        } else {

            Object object =
                    intent.getSerializableExtra(EXTRA_KYC);

            if (object instanceof KycRequest) {
                return (KycRequest) object;
            }
        }

        return null;
    }

    // ============================================================
    // INITIALIZE VIEWS
    // ============================================================

    private void initializeViews() {

        etAddress = findViewById(R.id.etAddress);

        etCity = findViewById(R.id.etCity);

        etState = findViewById(R.id.etState);

        etPincode = findViewById(R.id.etPincode);

        btnNext = findViewById(R.id.btnNext);
    }

    // ============================================================
    // CONFIGURE STATE DROPDOWN
    // ============================================================

    private void configureStateDropdown() {

        stateAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                states
        );

        etState.setAdapter(stateAdapter);

        etState.setThreshold(1);

        etState.setOnClickListener(v -> {

            etState.showDropDown();

        });

        etState.setOnItemClickListener(
                (parent, view, position, id) -> {

                    String state = stateAdapter.getItem(position);

                    if (state == null) {
                        return;
                    }

                    selectedState = state;

                    /*
                     * A previous city must never remain selected
                     * after the state changes.
                     */
                    selectedCity = null;

                    etCity.setText("", false);

                    /*
                     * Update the city list for the new state.
                     */
                    List<String> cities =
                            citiesByState.get(selectedState);

                    cityAdapter.clear();

                    if (cities != null) {
                        cityAdapter.addAll(cities);
                    }

                    cityAdapter.notifyDataSetChanged();

                    /*
                     * Enable the City field only after selecting
                     * a state from the dropdown.
                     */
                    etCity.setEnabled(true);

                    etCity.setFocusable(true);

                    etCity.setFocusableInTouchMode(true);

                    etCity.setHint("Search city");

                    etCity.setError(null);

                    etState.setError(null);

                    hideKeyboard();
                }
        );

        /*
         * If the user manually edits the state after selecting it,
         * invalidate the previous selection.
         *
         * A new dropdown selection will be required.
         */
        etState.addTextChangedListener(
                new android.text.TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        if (selectedState != null
                                && !selectedState.contentEquals(s)) {

                            selectedState = null;

                            selectedCity = null;

                            etCity.setText("", false);

                            etCity.setEnabled(false);

                            etCity.setHint("Select state first");

                            cityAdapter.clear();

                            cityAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void afterTextChanged(
                            android.text.Editable s
                    ) {
                    }
                }
        );
    }

    // ============================================================
    // CONFIGURE CITY DROPDOWN
    // ============================================================

    private void configureCityDropdown() {

        cityAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>()
        );

        etCity.setAdapter(cityAdapter);

        etCity.setThreshold(1);

        /*
         * City remains disabled until a state is selected.
         */
        etCity.setEnabled(false);

        etCity.setFocusable(false);

        etCity.setHint("Select state first");

        etCity.setOnClickListener(v -> {

            if (selectedState == null) {

                Toast.makeText(
                        AddressActivity.this,
                        "Please select a state first",
                        Toast.LENGTH_SHORT
                ).show();

                etState.requestFocus();

                etState.showDropDown();

                return;
            }

            etCity.showDropDown();
        });

        etCity.setOnItemClickListener(
                (parent, view, position, id) -> {

                    String city = cityAdapter.getItem(position);

                    if (city == null || selectedState == null) {
                        return;
                    }

                    selectedCity = city;

                    etCity.setError(null);

                    hideKeyboard();
                }
        );

        /*
         * If the user manually changes the selected city,
         * require another dropdown selection.
         */
        etCity.addTextChangedListener(
                new android.text.TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        if (selectedCity != null
                                && !selectedCity.contentEquals(s)) {

                            selectedCity = null;
                        }
                    }

                    @Override
                    public void afterTextChanged(
                            android.text.Editable s
                    ) {
                    }
                }
        );
    }

    // ============================================================
    // POPULATE EXISTING DATA
    // ============================================================

    private void populateExistingData() {

        if (request == null) {
            return;
        }

        if (request.getAddress() != null) {

            etAddress.setText(
                    request.getAddress()
            );
        }

        if (request.getPincode() != null) {

            etPincode.setText(
                    request.getPincode()
            );
        }

        String savedState =
                request.getState() == null
                        ? ""
                        : request.getState().trim();

        String savedCity =
                request.getCity() == null
                        ? ""
                        : request.getCity().trim();

        /*
         * Restore the state only when it exists in our dataset.
         */
        String canonicalState =
                findIgnoreCase(states, savedState);

        if (canonicalState == null) {

            etState.setText(savedState, false);

            etCity.setText(savedCity, false);

            selectedState = null;

            selectedCity = null;

            etCity.setEnabled(false);

            etCity.setFocusable(false);

            etCity.setHint("Select state first");

            return;
        }

        selectedState = canonicalState;

        etState.setText(
                canonicalState,
                false
        );

        /*
         * Load the cities belonging to the saved state.
         */
        List<String> cities =
                citiesByState.get(canonicalState);

        cityAdapter.clear();

        if (cities != null) {

            cityAdapter.addAll(cities);
        }

        cityAdapter.notifyDataSetChanged();

        etCity.setEnabled(true);

        etCity.setFocusable(true);

        etCity.setFocusableInTouchMode(true);

        etCity.setHint("Search city");

        /*
         * Restore the city only if it belongs to the selected state.
         */
        String canonicalCity =
                findIgnoreCase(cities, savedCity);

        if (canonicalCity != null) {

            selectedCity = canonicalCity;

            etCity.setText(
                    canonicalCity,
                    false
            );

        } else {

            /*
             * Keep the saved value visible, but require the user
             * to select a valid city before continuing.
             */
            selectedCity = null;

            etCity.setText(
                    savedCity,
                    false
            );
        }
    }

    // ============================================================
    // VALIDATE AND CONTINUE
    // ============================================================

    private void validateAndContinue() {

        if (request == null) {

            Toast.makeText(
                    this,
                    "KYC data is missing",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String address =
                etAddress.getText()
                        .toString()
                        .trim();

        String state =
                etState.getText()
                        .toString()
                        .trim();

        String city =
                etCity.getText()
                        .toString()
                        .trim();

        String pincode =
                etPincode.getText()
                        .toString()
                        .trim();

        // ========================================================
        // ADDRESS VALIDATION
        // ========================================================

        if (address.isEmpty()) {

            etAddress.setError(
                    "Address is required"
            );

            etAddress.requestFocus();

            return;
        }

        // ========================================================
        // STATE VALIDATION
        // ========================================================

        if (selectedState == null
                || !selectedState.equals(state)) {

            etState.setError(
                    "Select a state from the list"
            );

            etState.requestFocus();

            etState.showDropDown();

            return;
        }

        // ========================================================
        // CITY VALIDATION
        // ========================================================

        List<String> validCities =
                citiesByState.get(selectedState);

        if (selectedCity == null
                || !selectedCity.equals(city)
                || validCities == null
                || !validCities.contains(selectedCity)) {

            etCity.setError(
                    "Select a city from the list"
            );

            etCity.requestFocus();

            etCity.showDropDown();

            return;
        }

        // ========================================================
        // PIN CODE VALIDATION
        // ========================================================

        if (!pincode.matches("\\d{6}")) {

            etPincode.setError(
                    "Enter valid 6-digit PIN code"
            );

            etPincode.requestFocus();

            return;
        }

        // ========================================================
        // UPDATE SAME KYC REQUEST
        // ========================================================

        request.setAddress(address);

        request.setCity(selectedCity);

        request.setState(selectedState);

        request.setPincode(pincode);

        // ========================================================
        // EDIT MODE
        // ========================================================

        if (editMode) {

            Intent resultIntent = new Intent();

            resultIntent.putExtra(
                    EXTRA_KYC,
                    request
            );

            setResult(
                    RESULT_OK,
                    resultIntent
            );

            finish();

            return;
        }

        // ========================================================
        // NORMAL KYC FLOW
        // ========================================================

        Intent intent = new Intent(
                AddressActivity.this,
                EmploymentActivity.class
        );

        intent.putExtra(
                EXTRA_KYC,
                request
        );

        startActivity(intent);
    }

    // ============================================================
    // HIDE KEYBOARD
    // ============================================================

    private void hideKeyboard() {

        InputMethodManager imm =
                (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {

            imm.hideSoftInputFromWindow(
                    etCity.getWindowToken(),
                    0
            );
        }
    }

    // ============================================================
    // CASE-INSENSITIVE LOOKUP
    // ============================================================

    private static String findIgnoreCase(
            List<String> values,
            String target
    ) {

        if (values == null || target == null) {
            return null;
        }

        for (String value : values) {

            if (value.equalsIgnoreCase(target.trim())) {
                return value;
            }
        }

        return null;
    }

    // ============================================================
    // INDIAN STATES, UNION TERRITORIES AND SAMPLE CITIES
    // ============================================================

    private static Map<String, List<String>> createCityMap() {

        Map<String, List<String>> map =
                new LinkedHashMap<>();

        map.put("Andhra Pradesh", Arrays.asList(
                "Amaravati",
                "Visakhapatnam",
                "Vijayawada",
                "Guntur",
                "Nellore",
                "Tirupati",
                "Kurnool",
                "Rajahmundry",
                "Kakinada"
        ));

        map.put("Arunachal Pradesh", Arrays.asList(
                "Itanagar",
                "Naharlagun",
                "Tawang",
                "Pasighat"
        ));

        map.put("Assam", Arrays.asList(
                "Dispur",
                "Guwahati",
                "Silchar",
                "Dibrugarh",
                "Jorhat",
                "Tezpur"
        ));

        map.put("Bihar", Arrays.asList(
                "Patna",
                "Gaya",
                "Bhagalpur",
                "Muzaffarpur",
                "Darbhanga",
                "Purnia"
        ));

        map.put("Chhattisgarh", Arrays.asList(
                "Raipur",
                "Bhilai",
                "Bilaspur",
                "Korba",
                "Durg",
                "Jagdalpur"
        ));

        map.put("Goa", Arrays.asList(
                "Panaji",
                "Margao",
                "Vasco da Gama",
                "Mapusa",
                "Ponda"
        ));

        map.put("Gujarat", Arrays.asList(
                "Ahmedabad",
                "Surat",
                "Vadodara",
                "Rajkot",
                "Gandhinagar",
                "Bhavnagar",
                "Jamnagar",
                "Junagadh",
                "Anand",
                "Morbi"
        ));

        map.put("Haryana", Arrays.asList(
                "Chandigarh",
                "Gurugram",
                "Faridabad",
                "Panipat",
                "Ambala",
                "Hisar",
                "Karnal",
                "Rohtak",
                "Sonipat"
        ));

        map.put("Himachal Pradesh", Arrays.asList(
                "Shimla",
                "Dharamshala",
                "Manali",
                "Solan",
                "Mandi"
        ));

        map.put("Jharkhand", Arrays.asList(
                "Ranchi",
                "Jamshedpur",
                "Dhanbad",
                "Bokaro",
                "Deoghar",
                "Hazaribagh"
        ));

        map.put("Karnataka", Arrays.asList(
                "Bengaluru",
                "Mysuru",
                "Mangaluru",
                "Hubballi",
                "Dharwad",
                "Belagavi",
                "Kalaburagi",
                "Shivamogga",
                "Tumakuru",
                "Ballari"
        ));

        map.put("Kerala", Arrays.asList(
                "Thiruvananthapuram",
                "Kochi",
                "Kozhikode",
                "Thrissur",
                "Kollam",
                "Alappuzha",
                "Kannur",
                "Kottayam",
                "Palakkad"
        ));

        map.put("Madhya Pradesh", Arrays.asList(
                "Bhopal",
                "Indore",
                "Jabalpur",
                "Gwalior",
                "Ujjain",
                "Sagar",
                "Rewa",
                "Satna",
                "Dewas"
        ));

        map.put("Maharashtra", Arrays.asList(
                "Mumbai",
                "Pune",
                "Nagpur",
                "Nashik",
                "Thane",
                "Navi Mumbai",
                "Chhatrapati Sambhajinagar",
                "Solapur",
                "Kolhapur",
                "Amravati",
                "Nanded",
                "Sangli",
                "Jalgaon",
                "Akola",
                "Latur",
                "Ahmednagar"
        ));

        map.put("Manipur", Arrays.asList(
                "Imphal",
                "Thoubal",
                "Churachandpur",
                "Bishnupur"
        ));

        map.put("Meghalaya", Arrays.asList(
                "Shillong",
                "Tura",
                "Jowai",
                "Nongstoin"
        ));

        map.put("Mizoram", Arrays.asList(
                "Aizawl",
                "Lunglei",
                "Champhai",
                "Serchhip"
        ));

        map.put("Nagaland", Arrays.asList(
                "Kohima",
                "Dimapur",
                "Mokokchung",
                "Tuensang"
        ));

        map.put("Odisha", Arrays.asList(
                "Bhubaneswar",
                "Cuttack",
                "Rourkela",
                "Berhampur",
                "Sambalpur",
                "Puri"
        ));

        map.put("Punjab", Arrays.asList(
                "Chandigarh",
                "Ludhiana",
                "Amritsar",
                "Jalandhar",
                "Patiala",
                "Bathinda",
                "Pathankot",
                "Hoshiarpur"
        ));

        map.put("Rajasthan", Arrays.asList(
                "Jaipur",
                "Jodhpur",
                "Udaipur",
                "Kota",
                "Ajmer",
                "Bikaner",
                "Alwar",
                "Bharatpur",
                "Sikar"
        ));

        map.put("Sikkim", Arrays.asList(
                "Gangtok",
                "Namchi",
                "Gyalshing",
                "Mangan"
        ));

        map.put("Tamil Nadu", Arrays.asList(
                "Chennai",
                "Coimbatore",
                "Madurai",
                "Tiruchirappalli",
                "Salem",
                "Tirunelveli",
                "Erode",
                "Vellore",
                "Thanjavur",
                "Tiruppur"
        ));

        map.put("Telangana", Arrays.asList(
                "Hyderabad",
                "Warangal",
                "Nizamabad",
                "Karimnagar",
                "Khammam",
                "Mahbubnagar",
                "Suryapet"
        ));

        map.put("Tripura", Arrays.asList(
                "Agartala",
                "Udaipur",
                "Dharmanagar",
                "Kailashahar"
        ));

        map.put("Uttar Pradesh", Arrays.asList(
                "Lucknow",
                "Kanpur",
                "Ghaziabad",
                "Agra",
                "Varanasi",
                "Prayagraj",
                "Meerut",
                "Noida",
                "Bareilly",
                "Aligarh",
                "Moradabad",
                "Gorakhpur",
                "Jhansi"
        ));

        map.put("Uttarakhand", Arrays.asList(
                "Dehradun",
                "Haridwar",
                "Rishikesh",
                "Haldwani",
                "Roorkee",
                "Nainital"
        ));

        map.put("West Bengal", Arrays.asList(
                "Kolkata",
                "Howrah",
                "Durgapur",
                "Asansol",
                "Siliguri",
                "Bardhaman",
                "Kharagpur"
        ));

        // Union Territories

        map.put("Andaman and Nicobar Islands", Arrays.asList(
                "Port Blair"
        ));

        map.put("Chandigarh", Arrays.asList(
                "Chandigarh"
        ));

        map.put(
                "Dadra and Nagar Haveli and Daman and Diu",
                Arrays.asList(
                        "Daman",
                        "Silvassa",
                        "Diu"
                )
        );

        map.put("Delhi", Arrays.asList(
                "New Delhi",
                "Delhi"
        ));

        map.put("Jammu and Kashmir", Arrays.asList(
                "Srinagar",
                "Jammu",
                "Anantnag",
                "Baramulla"
        ));

        map.put("Ladakh", Arrays.asList(
                "Leh",
                "Kargil"
        ));

        map.put("Lakshadweep", Arrays.asList(
                "Kavaratti",
                "Agatti"
        ));

        map.put("Puducherry", Arrays.asList(
                "Puducherry",
                "Karaikal",
                "Mahe",
                "Yanam"
        ));

        return map;
    }

    // ============================================================
    // BACK BUTTON
    // ============================================================

    @Override
    public void onBackPressed() {

        if (editMode) {

            setResult(RESULT_CANCELED);

            finish();

            return;
        }

        super.onBackPressed();
    }
}