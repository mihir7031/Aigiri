const bcrypt = require('bcrypt');

async function runExample() {
    const password = 'hello123'; // simple password

    // Hash the password
    const hashedPassword = await bcrypt.hash(password, 10); // saltRounds = 10
    console.log('Hashed Password:', hashedPassword);

    // Compare the original password with the hash
    const valid = await bcrypt.compare(password, hashedPassword);
    console.log('Password is valid:', valid); // should log: true
}

runExample();
