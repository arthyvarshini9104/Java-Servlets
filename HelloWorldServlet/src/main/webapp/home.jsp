<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <title>Home Page</title>
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
        .home-container {
            background: #ffffff;
            padding: 30px 40px;
            border-radius: 12px;
            box-shadow: 0 8px 20px rgba(255, 255, 255, 0.2);
            width: 320px;
            border: 1px solid #ddd;
            color: #333;
            text-align: center;
        }
        h2 {
            font-weight: 700;
            margin-bottom: 24px;
            color: #000;
            letter-spacing: 1.5px;
        }
        p {
            margin-bottom: 24px;
            font-size: 1rem;
            color: #444;
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
    <div class="home-container">
        <h2>Welcome!</h2>
        <p>You have successfully logged in.</p>
        <form method="POST" action="Logout">
            <button type="submit">Logout</button>
        </form>
    </div>
</body>
</html>
