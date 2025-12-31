import { Component, OnInit, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Navbar } from '../../components/navbar/navbar';
import { Footer } from '../../components/footer/footer';
import { authService } from '../../services/auth';
import { MessageService, Message, ConversationUser } from '../../services/message.service';
import { API_BASE_URL } from '../../config/api.config';

interface Conversation {
  user: ConversationUser;
  lastMessage: string;
  lastMessageTime: Date;
  unreadCount: number;
}

@Component({
  selector: 'app-messages',
  standalone: true,
  imports: [CommonModule, FormsModule, Navbar, Footer],
  templateUrl: './messages.html',
  styleUrls: ['./messages.css']
})
export class Messages implements OnInit, AfterViewChecked {
  @ViewChild('messagesContainer') private messagesContainer!: ElementRef;

  conversations: Conversation[] = [];
  filteredConversations: Conversation[] = [];
  selectedConversation: ConversationUser | null = null;
  messages: Message[] = [];
  
  searchTerm: string = '';
  newMessage: string = '';
  currentUserId: number = 0;
  
  private shouldScrollToBottom = false;
  private messageRefreshInterval: any;

  constructor(
    private authService: authService,
    private messageService: MessageService,
    private router: Router,
    private route: ActivatedRoute,
    private http: HttpClient
  ) {}

 ngOnInit(): void {
  const currentUser = this.authService.getCurrentUser();
  if (!currentUser) {
    this.router.navigate(['/login']);
    return;
  }

  this.currentUserId = currentUser.id;
  this.loadConversations();

  // Check if we should open a specific conversation (from route params)
  this.route.queryParams.subscribe(params => {
    const userId = params['userId'];
    if (userId) {
      const targetUserId = Number(userId);
      
      // Wait for conversations to load, then check if conversation exists
      setTimeout(() => {
        const existingConv = this.conversations.find(c => c.user.id === targetUserId);
        
        if (existingConv) {
          // Conversation exists, select it
          this.selectConversationByUserId(targetUserId);
        } else {
          // No existing conversation, fetch user info and create new conversation
          this.startConversationWithUser(targetUserId);
        }
      }, 1000);
    }
  });
}

startConversationWithUser(userId: number): void {
  // Fetch user details from backend
  const token = this.authService.getToken();
  const headers = new HttpHeaders({
    'Authorization': `Bearer ${token}`
  });

  this.http.get<ConversationUser>(`${API_BASE_URL}/users/${userId}`, { headers }).subscribe({
    next: (user) => {
      console.log('✅ Fetched user for new conversation:', user);
      
      // Create a new conversation object
      const newConversation: Conversation = {
        user: user,
        lastMessage: 'Start a conversation...',
        lastMessageTime: new Date(),
        unreadCount: 0
      };

      // Add to conversations list if not already there
      const exists = this.conversations.find(c => c.user.id === user.id);
      if (!exists) {
        this.conversations.unshift(newConversation); // Add to top
        this.filteredConversations = [...this.conversations];
      }

      // Select this conversation
      this.selectedConversation = user;
      this.messages = [];
      this.shouldScrollToBottom = true;
    },
    error: (error) => {
      console.error('❌ Error fetching user:', error);
      alert('Could not load user information. Please try again.');
    }
  });
}
 

  ngAfterViewChecked(): void {
    if (this.shouldScrollToBottom) {
      this.scrollToBottom();
      this.shouldScrollToBottom = false;
    }
  }

  ngOnDestroy(): void {
    if (this.messageRefreshInterval) {
      clearInterval(this.messageRefreshInterval);
    }
  }

  loadConversations(): void {
    this.messageService.getConversations(this.currentUserId).subscribe({
      next: (users) => {
        console.log('✅ Loaded conversation users:', users);
        
        // Convert users to conversations
        this.conversations = users.map(user => ({
          user: user,
          lastMessage: 'Loading...',
          lastMessageTime: new Date(),
          unreadCount: 0
        }));

        // Load last message for each conversation
        this.conversations.forEach(conv => {
          this.messageService.getConversation(this.currentUserId, conv.user.id).subscribe({
            next: (messages) => {
              if (messages.length > 0) {
                const lastMsg = messages[messages.length - 1];
                conv.lastMessage = lastMsg.content;
                conv.lastMessageTime = new Date(lastMsg.sentAt);
                
                // Count unread
                conv.unreadCount = messages.filter(m => 
                  !m.isRead && m.receiver.id === this.currentUserId
                ).length;
              } else {
                conv.lastMessage = 'No messages yet';
              }
            }
          });
        });

        this.filteredConversations = [...this.conversations];
      },
      error: (error) => {
        console.error('❌ Error loading conversations:', error);
        this.conversations = [];
        this.filteredConversations = [];
      }
    });
  }

  selectConversation(conversation: Conversation): void {
    this.selectConversationByUserId(conversation.user.id);
  }

  selectConversationByUserId(userId: number): void {
    const conversation = this.conversations.find(c => c.user.id === userId);
    if (!conversation) return;

    this.selectedConversation = conversation.user;
    conversation.unreadCount = 0;
    this.loadMessages(userId);
    this.shouldScrollToBottom = true;

    // Start auto-refresh for this conversation
    if (this.messageRefreshInterval) {
      clearInterval(this.messageRefreshInterval);
    }
    
    this.messageRefreshInterval = setInterval(() => {
      this.loadMessages(userId, false); // false = don't scroll
    }, 3000); // Refresh every 3 seconds
  }

  loadMessages(userId: number, scroll: boolean = true): void {
    this.messageService.getConversation(this.currentUserId, userId).subscribe({
      next: (messages) => {
        console.log('✅ Loaded messages:', messages);
        this.messages = messages;
        
        if (scroll) {
          this.shouldScrollToBottom = true;
        }
      },
      error: (error) => {
        console.error('❌ Error loading messages:', error);
        this.messages = [];
      }
    });
  }

  sendMessage(): void {
    if (!this.newMessage.trim() || !this.selectedConversation) {
      return;
    }

    const content = this.newMessage.trim();
    this.newMessage = '';

    this.messageService.sendMessage(this.selectedConversation.id, content).subscribe({
      next: (message) => {
        console.log('✅ Message sent:', message);
        this.messages.push(message);
        
        // Update conversation last message
        const conv = this.conversations.find(c => c.user.id === this.selectedConversation?.id);
        if (conv) {
          conv.lastMessage = content;
          conv.lastMessageTime = new Date();
        }

        this.shouldScrollToBottom = true;
      },
      error: (error) => {
        console.error('❌ Error sending message:', error);
        alert('Failed to send message. Please try again.');
      }
    });
  }

  startNewConversation(): void {
    alert('To start a conversation, go to a tour page and click "Contact Guide"');
  }

  private scrollToBottom(): void {
    try {
      if (this.messagesContainer) {
        this.messagesContainer.nativeElement.scrollTop = this.messagesContainer.nativeElement.scrollHeight;
      }
    } catch (err) {
      console.error('Scroll to bottom failed:', err);
    }
  }

  // Search filter
  filterConversations(): void {
    this.filteredConversations = this.conversations.filter(conv =>
      conv.user.username.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }
}