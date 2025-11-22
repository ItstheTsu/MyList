<template class="page-full mouse-bg">
  <div class="page-full mouse-bg">
    <div class="form-container">
      <div class="welcome-msg">
        <h1>{{ welcomeTitle }}</h1>
        <div class="welcome-rotating">
          <p>{{ currentSubtitle }}</p>
        </div>
      </div>

      <div class="login-container">
        <h1>Login</h1>
        <form @submit.prevent="login">
          <div>
            <input v-model="email" type="email" placeholder="Email" required />
          </div>

          <div>
            <input
              v-model="password"
              type="password"
              placeholder="Senha"
              required
            />
          </div>

          <button type="submit">Entrar</button>

          <div class="register-redirect">
            <a @click="goToRegister">Não têm uma conta?</a>
          </div>

          <div class="recover-password">
            <a @click="backupPassword">Esqueceu a senha?</a>
          </div>
        </form>

        <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
      </div>
    </div>

    <canvas id="trail"></canvas>
  </div>
</template>

<script>
import api from "../services/api.js";
import { welcomeData } from "../data/welcome.js";
import "../styles/LoginRegister/LoginRegister.css";

export default {
  name: "Login",

  data() {
    return {
      email: "",
      password: "",
      errorMessage: "",

      welcomeTitle: welcomeData.title,
      subtitles: welcomeData.subtitle,
      currentSubtitle: welcomeData.subtitle[0],
      subtitleIndex: 0,
      typingSpeed: 70,
      deletingSpeed: 40,
      pauseTime: 1500,
    };
  },

  mounted() {
    const canvas = document.getElementById("trail");
    const ctx = canvas.getContext("2d");

    const resize = () => {
      canvas.width = window.innerWidth;
      canvas.height = window.innerHeight;
    };

    resize();
    window.addEventListener("resize", resize);

    let lastX = null;
    let lastY = null;

    const mouse = { x: 0, y: 0 };
    document.addEventListener("mousemove", (e) => {
      mouse.x = e.clientX;
      mouse.y = e.clientY;

      if (lastX === null) {
        lastX = e.clientX;
        lastY = e.clientY;
      }

      ctx.strokeStyle = "rgba(0,0,0,1)";
      ctx.lineWidth = 3;
      ctx.lineCap = "round";

      ctx.beginPath();
      ctx.moveTo(lastX, lastY);
      ctx.lineTo(e.clientX, e.clientY);
      ctx.stroke();

      lastX = e.clientX;
      lastY = e.clientY;
    });

    const fade = () => {
      ctx.fillStyle = "rgba(255,255,255,0.25)";
      ctx.fillRect(0, 0, canvas.width, canvas.height);
      requestAnimationFrame(fade);
    };

    const snakes = [];

    function createSnake() {
      const startX = Math.random() * canvas.width;
      const startY = Math.random() * canvas.height;

      return {
        points: Array.from({ length: 50 }, () => ({
          x: startX,
          y: startY,
        })),
        speed: 1 + Math.random() * 2,
        angle: Math.random() * Math.PI * 2,
        turnChance: 0.04 + Math.random() * 0.03,
        color: `rgba(${Math.floor(Math.random() * 255)}, 
                     ${Math.floor(Math.random() * 255)}, 
                     ${Math.floor(Math.random() * 255)}, 
                     ${0.3 + Math.random() * 0.4})`,
      };
    }

    for (let i = 0; i < 8; i++) snakes.push(createSnake());

    const particles = [];

    function explodeSnake(snake) {
      snake.points.forEach((p) => {
        for (let i = 0; i < 3; i++) {
          particles.push({
            x: p.x,
            y: p.y,
            vx: (Math.random() - 0.5) * 5,
            vy: (Math.random() - 0.5) * 5,
            alpha: 1,
            color: snake.color,
          });
        }
      });

      Object.assign(snake, createSnake());
    }

    function animateParticles() {
      particles.forEach((pt, i) => {
        pt.x += pt.vx;
        pt.y += pt.vy;
        pt.alpha -= 0.02;

        if (pt.alpha <= 0) {
          particles.splice(i, 1);
          return;
        }

        const rgb = pt.color.replace(/rgba?\(([^)]+)\)/, "$1").split(",");
        const r = rgb[0];
        const g = rgb[1];
        const b = rgb[2];

        ctx.fillStyle = `rgba(${r}, ${g}, ${b}, ${pt.alpha})`;
        ctx.beginPath();
        ctx.arc(pt.x, pt.y, 2, 0, Math.PI * 2);
        ctx.fill();
      });
    }

    function animateSnakes() {
      ctx.fillStyle = "rgba(255,255,255,0.06)";
      ctx.fillRect(0, 0, canvas.width, canvas.height);

      animateParticles();

      snakes.forEach((snake) => {
        if (Math.random() < snake.turnChance) {
          snake.angle += (Math.random() - 0.5) * 0.6;
        }

        const head = snake.points[0];

        const dx = head.x - mouse.x;
        const dy = head.y - mouse.y;
        const dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < 200) {
          explodeSnake(snake);
          return;
        }

        const newHead = {
          x: head.x + Math.cos(snake.angle) * snake.speed,
          y: head.y + Math.sin(snake.angle) * snake.speed,
        };

        snake.points.unshift(newHead);
        snake.points.pop();

        if (
          newHead.x < -50 ||
          newHead.x > canvas.width + 50 ||
          newHead.y < -50 ||
          newHead.y > canvas.height + 50
        ) {
          Object.assign(snake, createSnake());
        }

        ctx.beginPath();
        ctx.strokeStyle = snake.color;
        ctx.lineWidth = 3;
        ctx.lineCap = "round";
        ctx.lineJoin = "round";

        for (let i = 0; i < snake.points.length - 1; i++) {
          const p1 = snake.points[i];
          const p2 = snake.points[i + 1];
          ctx.moveTo(p1.x, p1.y);
          ctx.lineTo(p2.x, p2.y);
        }

        ctx.stroke();
      });

      requestAnimationFrame(animateSnakes);
    }

    animateSnakes();
    fade();
    this.typeWriter();
  },

  methods: {
    async login() {
      this.errorMessage = "";

      try {
        const response = await api.post("/auth/login", {
          email: this.email,
          password: this.password,
        });

        const user = response.data;
        const token = user.token;

        api.defaults.headers.Authorization = `Bearer ${token}`;
        localStorage.setItem("user", JSON.stringify(user));
        localStorage.setItem("token", token);

        this.$router.push("/dashboard");
      } catch (error) {
        console.error("Erro login:", error);

        if (error.response?.status === 401) {
          if (error.response.data === "Usuário não encontrado") {
            this.errorMessage = "Conta não encontrada, realize o cadastro.";
          } else {
            this.errorMessage = "Email ou senha inválidos!";
          }
        } else {
          this.errorMessage = "Erro inesperado. Tente novamente.";
        }
      }
    },

    goToRegister() {
      this.$router.push("/register");
    },

    backupPassword() {
      this.$router.push("/recover-password");
    },

    typeWriter() {
      const current = this.subtitles[this.subtitleIndex];
      let i = 0;

      const type = () => {
        if (i <= current.length) {
          this.currentSubtitle = current.slice(0, i);
          i++;
          setTimeout(type, this.typingSpeed);
        } else {
          setTimeout(() => this.deleteWriter(), this.pauseTime);
        }
      };

      type();
    },

    deleteWriter() {
      const current = this.subtitles[this.subtitleIndex];
      let i = current.length;

      const del = () => {
        if (i >= 0) {
          this.currentSubtitle = current.slice(0, i);
          i--;
          setTimeout(del, this.deletingSpeed);
        } else {
          this.subtitleIndex = (this.subtitleIndex + 1) % this.subtitles.length;
          this.typeWriter();
        }
      };

      del();
    },
  },
};
</script>