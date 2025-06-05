require('dotenv').config();

const express = require("express");
const admin = require("firebase-admin");
const jwt = require("jsonwebtoken");
const bcrypt = require("bcrypt");

admin.initializeApp({
    credential: admin.credential.cert(require(process.env.GOOGLE_APPLICATION_CREDENTIALS)),
});

const db = admin.firestore();
const app = express();
app.use(express.json());

const JWT_SECRET = process.env.JWT_SECRET;

// Login endpoint
app.post("/login", async (req, res) => {
    try {
        const { identifier, password } = req.body;

        // Query Firestore for user with identifier (e.g. username or email)
        const userQuery = await db
            .collection("User")
            .where("username", "==", identifier)
            .limit(1)
            .get();

        if (userQuery.empty) {
            return res.status(401).json({ error: "Invalid credentials" });
        }

        const userDoc = userQuery.docs[0];
        const userData = userDoc.data();

        // userData.password is assumed hashed with bcrypt
        const validPassword = await bcrypt.compare(password, userData.password);

        if (!validPassword) {
            return res.status(401).json({ error: password });
        }

        // Generate JWT token
        const token = jwt.sign(
            {
                userId: userDoc.id,
                identifier: userData.identifier,
            },
            JWT_SECRET,
            { expiresIn: "7d" }
        );

        // Send token and userId to client
        res.json({ token, userId: userDoc.id });
    } catch (error) {
        console.error("Login error", error);
        res.status(500).json({ error: "Internal server error" });
    }
});

// Example of protected route
app.get("/profile", async (req, res) => {
    try {
        const authHeader = req.headers.authorization;
        if (!authHeader || !authHeader.startsWith("Bearer ")) {
            return res.status(401).json({ error: "Unauthorized" });
        }

        const token = authHeader.substring(7);
        const decoded = jwt.verify(token, JWT_SECRET);

        const userDoc = await db.collection("User").doc(decoded.userId).get();
        if (!userDoc.exists) {
            return res.status(404).json({ error: "User not found" });
        }

        res.json(userDoc.data());
    } catch (error) {
        console.error("Profile error", error);
        res.status(401).json({ error: "Invalid or expired token" });
    }
});

const PORT = process.env.PORT || 3001;
app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
