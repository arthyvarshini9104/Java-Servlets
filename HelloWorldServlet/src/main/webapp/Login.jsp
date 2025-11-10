<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <title>Creative Login Page</title>
    <style>
        body {
            margin: 0;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #000000;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            color: #333333;
        }
        .login-container {
            background: #ffffff;
            padding: 30px 40px;
            border-radius: 12px;
            box-shadow: 0 8px 20px rgba(255, 255, 255, 0.2);
            width: 320px;
            border: 1px solid #ddd;
            color: #333;
        }
        h2 {
            text-align: center;
            font-weight: 700;
            margin-bottom: 24px;
            color: #000;
            letter-spacing: 1.5px;
        }
        label {
            display: block;
            margin-bottom: 6px;
            font-weight: 600;
            font-size: 0.9rem;
            color: #444;
        }
        input[type="text"], input[type="password"] {
            width: 100%;
            padding: 10px 12px;
            border-radius: 6px;
            border: 1px solid #ccc;
            margin-bottom: 18px;
            font-size: 1rem;
            transition: border-color 0.3s;
            background: #fff;
            color: #333;
        }
        input[type="text"]:focus, input[type="password"]:focus {
            border-color: #000;
            outline: none;
            background: #fff;
        }
        button {
            width: 100%;
            padding: 12px 0;
            border: none;
            border-radius: 8px;
            background-color: #000000;
            color: white;
            font-weight: 700;
            font-size: 1.1rem;
            cursor: pointer;
            transition: background-color 0.4s;
        }
        button:hover {
            background-color: #333333;
        }
    </style>
</head>
<body>

    <div class="login-container">
        <h2>Welcome Back</h2>
        <form method="POST" action="Login">
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" placeholder="Enter your username" required />
            
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" placeholder="Enter your password" required />
            
            <button type="submit">Login</button>
        </form>
    </div>

</body>
</html>
